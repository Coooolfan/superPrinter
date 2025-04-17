package com.coooolfan.superprinter.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatus {

    /**
     * 已创建
     */
    CREATED("CREATED", "已创建"),

    /**
     * 已支付
     */
    PAID("PAID", "已支付"),

    /**
     * 处理中
     */
    PROCESSING("PROCESSING", "处理中"),

    /**
     * 待取件
     */
    READY_FOR_PICKUP("READY_FOR_PICKUP", "待取件"),

    /**
     * 已完成
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消");

    /**
     * 状态编码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String desc;

    OrderStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}