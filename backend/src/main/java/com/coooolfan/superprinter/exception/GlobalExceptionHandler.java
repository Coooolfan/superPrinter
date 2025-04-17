package com.coooolfan.superprinter.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.coooolfan.superprinter.vo.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    /**
     * 处理Sa-Token未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ErrorResponse> handleNotLoginException(NotLoginException e) {
        log.error("未登录异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, "未登录或登录已过期"));
    }

    /**
     * 处理Sa-Token无权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<ErrorResponse> handleNotPermissionException(NotPermissionException e) {
        log.error("无权限异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, "无此权限: " + e.getPermission()));
    }

    /**
     * 处理Sa-Token无角色异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ResponseEntity<ErrorResponse> handleNotRoleException(NotRoleException e) {
        log.error("无角色异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, "无此角色: " + e.getRole()));
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "系统异常，请稍后再试"));
    }
}