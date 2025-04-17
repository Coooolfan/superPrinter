package com.coooolfan.superprinter.vo.response;


import com.coooolfan.superprinter.entity.PrinterResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打印机资源响应实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrinterResourceResponse {
    /**
     * 打印机ID
     */
    private Long printerId;

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 构造方法：从 PrinterResource 实体类转换为响应实体类
     */
    public PrinterResourceResponse(PrinterResource printerResource) {
        PrinterResourceResponse response = new PrinterResourceResponse();
        response.setPrinterId(printerResource.getPrinterId());
        response.setPrinterName(printerResource.getPrinterName());
        response.setStatus(printerResource.getStatus());
        response.setPaperCount(printerResource.getPaperCount());
        response.setCreateTime(printerResource.getCreateTime());
        response.setUpdateTime(printerResource.getUpdateTime());
    }
}
