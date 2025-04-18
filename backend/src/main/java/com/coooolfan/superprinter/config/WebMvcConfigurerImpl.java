package com.coooolfan.superprinter.config;

import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置类
 */
@Configuration
public class WebMvcConfigurerImpl implements WebMvcConfigurer {
    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
        // 添加请求日志拦截器
        registry.addInterceptor(new RequestLogInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public SaCorsHandleFunction corsHandle() {
        return (req, res, sto) -> {
            res
                    .setHeader("Access-Control-Allow-Origin", "*") // 允许指定域访问跨域资源
                    .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE") // 允许所有请求方式
                    .setHeader("Access-Control-Max-Age", "3600") // 有效时间
                    .setHeader("Access-Control-Allow-Headers", "*"); // 允许的header参数

            // 如果是预检请求，则立即返回到前端
            SaRouter.match(SaHttpMethod.OPTIONS).free(r -> System.out.println("--------OPTIONS预检请求，不做处理")).back();
        };
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
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            int status = response.getStatus();
            log.info("请求完成: {} {} - {}", method, requestURI, status);
        }
    }
}