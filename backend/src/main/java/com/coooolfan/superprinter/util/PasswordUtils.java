package com.coooolfan.superprinter.util;

import cn.dev33.satoken.secure.SaSecureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 */
@Component
public class PasswordUtils {

    @Value("${password.salt}")
    private String salt;

    /**
     * 加密密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public String encode(String password) {
        return SaSecureUtil.sha256(password + salt);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            String hashedInput = SaSecureUtil.sha256(rawPassword + salt);
            return hashedInput.equals(encodedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}