package com.coooolfan.superprinter.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 创建业务异常
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 创建业务异常（默认错误码为400）
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        this(400, message);
    }
}