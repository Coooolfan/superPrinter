package com.coooolfan.superprinter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加请求日志拦截器
        registry.addInterceptor(new RequestLogInterceptor())
                .addPathPatterns("/**");
    }

    /**
     * 请求日志拦截器
     */
    @Slf4j
    public static class RequestLogInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            log.info("收到请求: {} {}", method, requestURI);
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                Exception ex) {
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            int status = response.getStatus();
            log.info("请求完成: {} {} - {}", method, requestURI, status);
        }
    }
}