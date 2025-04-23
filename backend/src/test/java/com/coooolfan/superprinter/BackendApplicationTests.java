package com.coooolfan.superprinter;

import com.coooolfan.superprinter.util.Minio.MinioUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads() throws Exception {
        // 方法1：使用redisTemplate.opsForHash()直接获取
        String key = "idempotent:e92ce250-1381-4aa3-9585-da9ac150b08d";
        
        // 获取存在性检查
        Boolean exists = redisTemplate.hasKey(key);
        System.out.println("Key exists: " + exists);
        
        // 直接使用RedisTemplate提供的Hash操作方法
        Map<Object, Object> hashEntries = redisTemplate.opsForHash().entries(key);
        System.out.println("Hash entries using RedisTemplate: " + hashEntries);
        
        // 方法2：执行Lua脚本并正确处理结果
        String luaScript = "return redis.call('HGET', KEYS[1])";
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(String.class);  // 指定返回类型为List
        List<String> keys = Collections.singletonList(key);
        String result = redisTemplate.execute(script, keys);
        System.out.println("Hash entries using Lua script: " + result);
        

    }

}
