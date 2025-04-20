package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.config.RabbitMQConfig;
import com.coooolfan.superprinter.entity.PrintOrder;
import com.coooolfan.superprinter.mapper.PrintOrderMapper;
import com.coooolfan.superprinter.vo.OrderPreTokenVO;
import com.coooolfan.superprinter.vo.message.FilePageCountMessage;
import com.coooolfan.superprinter.vo.response.OrderPreBalanceResponse;
import com.coooolfan.superprinter.vo.response.OrderPreTokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.coooolfan.superprinter.config.RedisConfig.PREFIX_IDEMPOTENT_COUNT;

/**
 * 打印订单服务实现类
 */
@Service
@AllArgsConstructor
public class PrintOrderService extends ServiceImpl<PrintOrderMapper, PrintOrder> implements IService<PrintOrder> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public OrderPreTokenResponse getOrderPreToken(OrderPreTokenVO vo) {
        // 生成一串UUID存到Redis中，用于接口幂等性
        String token = UUID.randomUUID().toString();
        // 键名为idempotent:token，值为此订单对应的文件总页数
        // （不考虑多份打印、不考虑双面复印。docx和pdf以真实页数计算，其他任意文件均当作单页图片计算）
        redisTemplate.opsForValue().set(PREFIX_IDEMPOTENT_COUNT + token, -1, 5, TimeUnit.MINUTES);

        // 向消息队列投递页数统计任务，使用专门的消息传输对象
        FilePageCountMessage message = new FilePageCountMessage();
        message.setTimestamp(Instant.now().toEpochMilli());
        message.setToken(token);
        message.setFileIds(String.join(",", vo.getFileIds()));
        message.setRetryCount(0); // 初始化重试次数为0

        // 发送消息到RabbitMQ，使用文件页数统计的路由键
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRINT_EXCHANGE,
                RabbitMQConfig.FILE_PAGE_COUNT_ROUTING_KEY,
                message
        );

        return new OrderPreTokenResponse(token);
    }

    public OrderPreBalanceResponse getOrderPreBalance(String uuid) {
        // 从Redis中获取对应的文件总页数
        Integer pageCount =(Integer) redisTemplate.opsForValue().get(PREFIX_IDEMPOTENT_COUNT + uuid);
        return new OrderPreBalanceResponse(Objects.requireNonNullElse(pageCount, -1));
    }
}