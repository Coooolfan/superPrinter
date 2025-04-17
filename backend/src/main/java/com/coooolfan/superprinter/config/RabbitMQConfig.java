package com.coooolfan.superprinter.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    // 业务交换机
    public static final String PRINT_EXCHANGE = "print.exchange";
    // 死信交换机
    public static final String PRINT_DLX_EXCHANGE = "print.dlx.exchange";

    // 文件页数统计队列
    public static final String FILE_PAGE_COUNT_QUEUE = "file.page.count.queue";
    // 文件页数统计死信队列
    public static final String FILE_PAGE_COUNT_DLX_QUEUE = "file.page.count.dlx.queue";

    // 订单处理队列
    public static final String ORDER_PROCESS_QUEUE = "order.process.queue";
    // 订单处理死信队列
    public static final String ORDER_PROCESS_DLX_QUEUE = "order.process.dlx.queue";

    // 路由KEY
    public static final String FILE_PAGE_COUNT_ROUTING_KEY = "file.page.count";
    public static final String ORDER_PROCESS_ROUTING_KEY = "order.process";

    /**
     * 配置RabbitTemplate，使用JSON序列化消息
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * 业务交换机
     */
    @Bean
    public DirectExchange printExchange() {
        return new DirectExchange(PRINT_EXCHANGE);
    }

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange printDlxExchange() {
        return new DirectExchange(PRINT_DLX_EXCHANGE);
    }

    /**
     * 文件页数统计队列
     */
    @Bean
    public Queue filePageCountQueue() {
        Map<String, Object> args = new HashMap<>();
        // 绑定死信交换机
        args.put("x-dead-letter-exchange", PRINT_DLX_EXCHANGE);
        // 绑定死信路由键
        args.put("x-dead-letter-routing-key", FILE_PAGE_COUNT_ROUTING_KEY);
        return new Queue(FILE_PAGE_COUNT_QUEUE, true, false, false, args);
    }

    /**
     * 文件页数统计死信队列
     */
    @Bean
    public Queue filePageCountDlxQueue() {
        return new Queue(FILE_PAGE_COUNT_DLX_QUEUE);
    }

    /**
     * 订单处理队列
     */
    @Bean
    public Queue orderProcessQueue() {
        Map<String, Object> args = new HashMap<>();
        // 绑定死信交换机
        args.put("x-dead-letter-exchange", PRINT_DLX_EXCHANGE);
        // 绑定死信路由键
        args.put("x-dead-letter-routing-key", ORDER_PROCESS_ROUTING_KEY);
        return new Queue(ORDER_PROCESS_QUEUE, true, false, false, args);
    }

    /**
     * 订单处理死信队列
     */
    @Bean
    public Queue orderProcessDlxQueue() {
        return new Queue(ORDER_PROCESS_DLX_QUEUE);
    }

    /**
     * 绑定文件页数统计队列到业务交换机
     */
    @Bean
    public Binding bindingFilePageCountQueue() {
        return BindingBuilder.bind(filePageCountQueue())
                .to(printExchange())
                .with(FILE_PAGE_COUNT_ROUTING_KEY);
    }

    /**
     * 绑定文件页数统计死信队列到死信交换机
     */
    @Bean
    public Binding bindingFilePageCountDlxQueue() {
        return BindingBuilder.bind(filePageCountDlxQueue())
                .to(printDlxExchange())
                .with(FILE_PAGE_COUNT_ROUTING_KEY);
    }

    /**
     * 绑定订单处理队列到业务交换机
     */
    @Bean
    public Binding bindingOrderProcessQueue() {
        return BindingBuilder.bind(orderProcessQueue())
                .to(printExchange())
                .with(ORDER_PROCESS_ROUTING_KEY);
    }

    /**
     * 绑定订单处理死信队列到死信交换机
     */
    @Bean
    public Binding bindingOrderProcessDlxQueue() {
        return BindingBuilder.bind(orderProcessDlxQueue())
                .to(printDlxExchange())
                .with(ORDER_PROCESS_ROUTING_KEY);
    }
}