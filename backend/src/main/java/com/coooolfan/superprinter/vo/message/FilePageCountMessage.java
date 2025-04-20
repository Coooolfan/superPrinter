package com.coooolfan.superprinter.vo.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件页数统计消息传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilePageCountMessage implements Serializable {
    
    /**
     * 消息时间戳
     */
    private Long timestamp;
    
    /**
     * 幂等性令牌
     */
    private String token;
    
    /**
     * 文件ID列表，以逗号分隔
     */
    private String fileIds;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 0;
}