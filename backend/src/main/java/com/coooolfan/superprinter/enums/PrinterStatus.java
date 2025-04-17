package com.coooolfan.superprinter.enums;

import lombok.Getter;

/**
 * 打印机状态枚举
 */
@Getter
public enum PrinterStatus {

    /**
     * 在线
     */
    ONLINE("ONLINE", "在线"),

    /**
     * 离线
     */
    OFFLINE("OFFLINE", "离线"),

    /**
     * 缺纸
     */
    OUT_OF_PAPER("OUT_OF_PAPER", "缺纸");

    /**
     * 状态编码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String desc;

    PrinterStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}