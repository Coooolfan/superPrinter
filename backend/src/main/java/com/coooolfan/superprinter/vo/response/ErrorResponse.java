package com.coooolfan.superprinter.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误响应类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误消息
     */
    private String message;
}