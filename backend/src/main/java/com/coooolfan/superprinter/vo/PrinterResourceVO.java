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
}