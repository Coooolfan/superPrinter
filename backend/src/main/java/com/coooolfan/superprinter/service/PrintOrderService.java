package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.config.RabbitMQConfig;
import com.coooolfan.superprinter.entity.PrintOrder;
import com.coooolfan.superprinter.mapper.PrintOrderMapper;
import com.coooolfan.superprinter.vo.OrderPreTokenVO;
import com.coooolfan.superprinter.vo.PrintOrderCreateVO;
import com.coooolfan.superprinter.vo.message.FilePageCountMessage;
import com.coooolfan.superprinter.vo.response.OrderPreBalanceResponse;
import com.coooolfan.superprinter.vo.response.OrderPreTokenResponse;
import com.coooolfan.superprinter.vo.response.PrintOrderCreateResponse;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        String redisKey = PREFIX_IDEMPOTENT_COUNT + token;
        
        // 使用Hash操作存储多个字段
        redisTemplate.opsForHash().put(redisKey, "fileIds", vo.getFileIds());
        redisTemplate.opsForHash().put(redisKey, "pageCount", "-1"); // 初始页数设为-1，后续由异步任务更新
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        
        // 投递页数统计任务
        FilePageCountMessage message = new FilePageCountMessage();
        message.setTimestamp(Instant.now().toEpochMilli());
        message.setToken(token);
        message.setFileIds(vo.getFileIds());
        message.setRetryCount(0); // 初始化重试次数为0

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRINT_EXCHANGE,
                RabbitMQConfig.FILE_PAGE_COUNT_ROUTING_KEY,
                message
        );

        return new OrderPreTokenResponse(token);
    }

    public OrderPreBalanceResponse getOrderPreBalance(String uuid) {
        // 从Redis Hash中获取对应的文件总页数
        String pageCountStr = (String) redisTemplate.opsForHash().get(PREFIX_IDEMPOTENT_COUNT + uuid, "pageCount");
        int pageCount = -1;
        
        try {
            if (pageCountStr != null) {
                pageCount = Integer.parseInt(pageCountStr);
            }
        } catch (NumberFormatException e) {
            // 转换失败时保持默认值-1
        }
        
        return new OrderPreBalanceResponse(pageCount);
    }


    public PrintOrderCreateResponse createOrder(PrintOrderCreateVO vo) {
        // 幂等、分布式锁、乐观锁
        // TODO
        return new PrintOrderCreateResponse();
    }



}