package com.coooolfan.superprinter.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息响应类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    /**
     * 响应消息
     */
    private String message;
}