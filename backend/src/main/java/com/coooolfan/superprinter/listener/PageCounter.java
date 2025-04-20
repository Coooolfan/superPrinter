package com.coooolfan.superprinter.listener;

import com.coooolfan.superprinter.config.RabbitMQConfig;
import com.coooolfan.superprinter.vo.message.FilePageCountMessage;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.coooolfan.superprinter.config.RedisConfig.PREFIX_IDEMPOTENT_COUNT;

@Slf4j
@Component
@RequiredArgsConstructor
public class PageCounter {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    
    private static final int MAX_RETRY_COUNT = 3;
    private static final long EXPIRATION_TIME_MILLIS = 5 * 60 * 1000 - 5000; // 5分钟减5秒
    private static final Random random = new Random();

    @RabbitListener(queues = RabbitMQConfig.FILE_PAGE_COUNT_QUEUE)
    public void processMessage(FilePageCountMessage message, Channel channel, Message amqpMessage) throws IOException {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
        try {
            log.info("接收到文件页数统计任务：{}", message);
            
            // 1. 判断时间戳是否已过期，5min-5s就算过期，扔到死信队列
            long currentTimeMillis = Instant.now().toEpochMilli();
            if (currentTimeMillis - message.getTimestamp() > EXPIRATION_TIME_MILLIS) {
                /*
                 * 优先处理新消息而非过期消息
                 * 如果继续处理过期消息，会导致所有新消息都被延迟处理，所有订单统计都被延迟
                 * 直接拒绝过期消息，放弃这部分消息，避免影响新消息的处理（降级）
                 */
                log.warn("文件页数统计任务已过期，发送到死信队列，token：{}", message.getToken());
                // 拒绝消息，不重新入队，会自动发送到死信队列
                channel.basicNack(deliveryTag, false, false);
                return;
            }
            
            // 2. 判断是第几次重试，重试次数超过3次就算失败，扔到死信队列
            if (message.getRetryCount() >= MAX_RETRY_COUNT) {
                log.error("文件页数统计任务重试次数超过{}次，发送到死信队列，token：{}", MAX_RETRY_COUNT, message.getToken());
                // 拒绝消息，不重新入队，会自动发送到死信队列
                channel.basicNack(deliveryTag, false, false);
                return;
            }
            
            // 3. MOCK：生成随机数当作统计结果
            String[] fileIds = message.getFileIds().split(",");
            int totalPages = 0;
            
            for (String fileId : fileIds) {
                // 模拟每个文件的页数为1-10页
                int pageCount = random.nextInt(10) + 1;
                totalPages += pageCount;
                log.info("文件{}的页数为：{}", fileId, pageCount);
            }
            
            /*
             * 4. 更新到Redis中
             * 这里没有预判断kv是否过期。符合业务预期。
             * redis中的kv有两个作用：key用于幂等性控制、value存储订单页数。
             * - 过期时间是副作用，顺便用来控制用户在订单页超时，不参与数据写。
             * 
             * kv在此时过期说明mq负载过高，消息堆积，导致处理时间过长，正常情况kv在此时不会过期
             * 这种情况下直接set即可，超时时间会被重置，无副作用
             */
            String redisKey = PREFIX_IDEMPOTENT_COUNT + message.getToken();
            redisTemplate.opsForValue().set(redisKey, totalPages, 5, TimeUnit.MINUTES);
            log.info("更新Redis中的文件页数统计结果，token：{}，总页数：{}", message.getToken(), totalPages);
            
            // 5. 完成消费
            channel.basicAck(deliveryTag, false);
            log.info("文件页数统计任务处理完成，token：{}", message.getToken());
            
        } catch (Exception e) {
            // 发生异常，重试次数+1，重新放入队列中
            log.error("文件页数统计任务处理异常，token：{}，异常信息：{}", message.getToken(), e.getMessage(), e);
            
            try {
                // 拒绝当前消息
                channel.basicNack(deliveryTag, false, false);
                
                // 增加重试次数并重新发送消息
                message.setRetryCount(message.getRetryCount() + 1);
                message.setTimestamp(Instant.now().toEpochMilli()); // 更新时间戳
                
                // 重新发送到原队列
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.PRINT_EXCHANGE,
                        RabbitMQConfig.FILE_PAGE_COUNT_ROUTING_KEY,
                        message
                );
                
                log.info("文件页数统计任务已重新放入队列，当前重试次数：{}", message.getRetryCount());
            } catch (Exception ex) {
                log.error("重新发送消息失败", ex);
            }
        }
    }
}
