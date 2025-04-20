package com.coooolfan.superprinter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.coooolfan.superprinter.vo.response.OrderPreTokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@AllArgsConstructor
@RequestMapping("/api/print-order-pre")
@SaCheckLogin
public class PrintOrderPreController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    public OrderPreTokenResponse getOrderPreToken() {
//        生成一串UUID存到Redis中，用于接口幂等性
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("idempotent:" + token, token, 5, TimeUnit.MINUTES);
        return new OrderPreTokenResponse(token);
    }
}
