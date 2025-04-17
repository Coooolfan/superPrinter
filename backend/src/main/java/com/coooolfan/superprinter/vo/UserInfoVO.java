package com.coooolfan.superprinter.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户信息VO对象
 */
@Data
public class UserInfoVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户角色
     */
    private Integer role;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 账户余额
     */
    private BigDecimal balance;
}