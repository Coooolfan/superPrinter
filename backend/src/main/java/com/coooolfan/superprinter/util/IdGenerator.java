package com.coooolfan.superprinter.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * ID生成器
 */
@Component
public class IdGenerator {

    @Value("${app.instanceId}")
    private String instanceId;

    public String generateId() {
        // 生成一个唯一的ID，格式为 instanceId + "-" + UUID
        return instanceId + "-" + java.util.UUID.randomUUID().toString();
    }
}