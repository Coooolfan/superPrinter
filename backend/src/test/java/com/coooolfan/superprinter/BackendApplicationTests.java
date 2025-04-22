package com.coooolfan.superprinter;

import com.coooolfan.superprinter.util.Minio.MinioUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() throws Exception {
        DefaultRedisScript<String> CREATE_ORDER_LOCK_SCRIPT = new DefaultRedisScript<>();
        CREATE_ORDER_LOCK_SCRIPT.setScriptText("return redis.call('HGET', KEYS[1], 'pageCount')");
        CREATE_ORDER_LOCK_SCRIPT.setResultType(String.class);
        String hashKey = "idempotent:330f6ddc-8f77-4bd7-8ede-b46b1057c310";
        System.out.println(redisTemplate.execute(CREATE_ORDER_LOCK_SCRIPT, List.of(hashKey)));
    }

}
