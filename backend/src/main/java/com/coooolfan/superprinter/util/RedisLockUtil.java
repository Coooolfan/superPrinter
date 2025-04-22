package com.coooolfan.superprinter.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 */
@Component
@RequiredArgsConstructor
public class RedisLockUtil {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>();
    private static final DefaultRedisScript<Long> CREATE_ORDER_LOCK_SCRIPT = new DefaultRedisScript<>();
    
    static {
        // 初始化解锁脚本
        UNLOCK_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/unlock.lua")));
        UNLOCK_SCRIPT.setResultType(Long.class);
        
        // 初始化订单创建锁脚本
        CREATE_ORDER_LOCK_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/createOrderLock.lua")));
        CREATE_ORDER_LOCK_SCRIPT.setResultType(Long.class);
    }
    
    /**
     * 尝试获取分布式锁
     * @param lockKey 锁键
     * @param lockValue 锁值（通常是请求的唯一标识）
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     * @return 是否成功获取锁
     */
    public boolean tryLock(String lockKey, String lockValue, long expireTime, TimeUnit timeUnit) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTime, timeUnit);
        return Boolean.TRUE.equals(success);
    }
    
    /**
     * 释放分布式锁（通过Lua脚本保证原子性）
     * @param lockKey 锁键
     * @param lockValue 锁值（必须与获取锁时的值一致）
     * @return 是否成功释放锁
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        Long result = redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);
        return result != null && result == 0;
    }
    
    /**
     * 创建订单锁定校验（幂等性检查 + 分布式锁）
     * @param token 幂等性token
     * @param lockKey 分布式锁key
     * @param lockValue 分布式锁value
     * @return 0-成功, 1-幂等token不存在, 2-页数计算未完成, 3-锁已被占用
     */
    public int createOrderLock(String token, String lockKey, String lockValue) {
        List<String> keys = Arrays.asList(token, lockKey);
        Long result = redisTemplate.execute(CREATE_ORDER_LOCK_SCRIPT, keys, lockValue);
        return result != null ? result.intValue() : -1;
    }
    
    /**
     * 根据createOrderLock返回的错误码获取错误消息
     * @param errorCode 错误码
     * @return 错误消息
     */
    public String getCreateOrderLockErrorMessage(int errorCode) {
        switch (errorCode) {
            case -1:
                return "订单token无效或已过期";
            case -2:
                return "文件页数计算尚未完成，请稍后再试";
            case -3:
                return "操作过于频繁，请稍后再试";
            default:
                return "未知错误";
        }
    }
}