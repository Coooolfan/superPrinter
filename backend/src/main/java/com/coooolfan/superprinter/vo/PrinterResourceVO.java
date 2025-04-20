package com.coooolfan.superprinter.vo;

import lombok.Data;

/**
 * 打印机资源VO
 */
@Data
public class PrinterResourceVO {

    /**
     * 打印机名称
     */
    private String printerName;

    /**
     * 打印机状态：ONLINE,OFFLINE,OUT_OF_PAPER
     */
    private String status;

    /**
     * A纸张数量
     */
    private Integer paperCount;

    /**
     * 支持彩色打印
     */
    private Boolean supportColor;

    /**
     * 支持双面打印
     */
    private Boolean supportDuplex;

    /**
     * 支持的纸张类型
     * 例如：A4,A5,Letter,Legal 以逗号分隔
     */
    private String paperType;
}