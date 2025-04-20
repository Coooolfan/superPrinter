package com.coooolfan.superprinter.listener;

import com.coooolfan.superprinter.config.RabbitMQConfig;
import com.coooolfan.superprinter.vo.message.FilePageCountMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 死信队列消费者
 * 负责处理所有进入死信队列的消息，记录日志
 */
@Slf4j
@Component
public class DeadLetterQueueListener {

    private final Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 处理文件页数统计死信队列的消息
     */
    @RabbitListener(queues = RabbitMQConfig.FILE_PAGE_COUNT_DLX_QUEUE)
    public void processFilePageCountDeadLetter(Message message) throws IOException {
        try {
            // 转换消息内容
            FilePageCountMessage filePageCountMessage =
                    (FilePageCountMessage) converter.fromMessage(message, FilePageCountMessage.class);
            
            // 计算消息在队列中的存活时间
            long currentTime = Instant.now().toEpochMilli();
            long messageTime = filePageCountMessage.getTimestamp();
            long lifeTimeSeconds = (currentTime - messageTime) / 1000;
            
            // 获取消息创建时间的可读格式
            LocalDateTime messageDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(messageTime), 
                    ZoneId.systemDefault()
            );
            
            // 记录详细的死信消息日志
            log.error("""
                            【死信队列警告】文件页数统计任务进入死信队列：
                            Token: {}
                            文件列表: {}
                            重试次数: {}
                            消息创建时间: {}
                            消息生存时间: {}秒
                            可能原因: {}""",
                    filePageCountMessage.getToken(),
                    filePageCountMessage.getFileIds(),
                    filePageCountMessage.getRetryCount(),
                    messageDateTime.format(DATE_TIME_FORMATTER),
                    lifeTimeSeconds,
                    getFailureReason(filePageCountMessage, lifeTimeSeconds)
            );
            
            // 这里可以添加告警通知逻辑，如发送邮件、短信等
            
        } catch (Exception e) {
            log.error("处理文件页数统计死信队列消息失败", e);
        }
    }
    
    /**
     * 处理订单处理死信队列的消息
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_PROCESS_DLX_QUEUE)
    public void processOrderProcessDeadLetter(Message message) {
        // 订单处理死信队列的处理逻辑，根据实际情况实现
        log.error("【死信队列警告】订单处理任务进入死信队列，消息内容：{}", message);
    }
    
    /**
     * 分析消息失败的可能原因
     */
    private String getFailureReason(FilePageCountMessage message, long lifeTimeSeconds) {
        if (message.getRetryCount() >= 3) {
            return "重试次数超过最大限制(3次)";
        } else if (lifeTimeSeconds > 295) { // 5分钟减5秒 = 295秒
            return "消息处理超时";
        } else {
            return "未知原因，可能是系统异常";
        }
    }
}