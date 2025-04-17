package com.coooolfan.superprinter.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {

    /**
     * 普通用户
     */
    USER(0, "普通用户"),

    /**
     * 商户
     */
    MERCHANT(1, "商户"),

    /**
     * 管理员
     */
    ADMIN(2, "管理员");

    /**
     * 角色编码
     */
    private final Integer code;

    /**
     * 角色描述
     */
    private final String desc;

    UserRole(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取角色枚举
     * 
     * @param code 角色编码
     * @return 角色枚举
     */
    public static UserRole getByCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (UserRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }

        return null;
    }
}