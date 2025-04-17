package com.coooolfan.superprinter.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 */
@Component
public class RedisLockUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // 释放锁的Lua脚本
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>();

    // 锁前缀
    private static final String LOCK_PREFIX = "lock:";

    // 幂等前缀
    private static final String IDEMPOTENT_PREFIX = "idempotent:";

    static {
        UNLOCK_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/unlock.lua")));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public RedisLockUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey    锁的键
     * @param requestId  请求标识
     * @param expireTime 过期时间
     * @param timeUnit   时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime, TimeUnit timeUnit) {
        String key = LOCK_PREFIX + lockKey;
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, requestId, expireTime, timeUnit);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁的键
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        String key = LOCK_PREFIX + lockKey;
        Long result = redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(key), requestId);
        return result != null && result.equals(1L);
    }

    /**
     * 检查幂等性
     *
     * @param businessKey 业务键
     * @param expireTime  过期时间
     * @param timeUnit    时间单位
     * @return 是否是第一次请求
     */
    public boolean checkIdempotent(String businessKey, long expireTime, TimeUnit timeUnit) {
        String key = IDEMPOTENT_PREFIX + businessKey;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, 1, expireTime, timeUnit));
    }

    /**
     * 生成请求标识
     *
     * @return 请求标识
     */
    public static String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 构建业务场景的锁键
     *
     * @param scene    业务场景
     * @param userId   用户ID
     * @param targetId 目标资源ID
     * @return 锁键
     */
    public static String buildLockKey(String scene, Long userId, Long targetId) {
        return scene + ":" + userId + ":" + targetId;
    }

    /**
     * 构建业务场景的幂等键
     *
     * @param scene  业务场景
     * @param unique 唯一标识
     * @return 幂等键
     */
    public static String buildIdempotentKey(String scene, String unique) {
        return scene + ":" + unique;
    }
}