package com.coooolfan.superprinter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打印机资源实体类
 */
@Data
@TableName("printer_resource")
public class PrinterResource {
    /**
     * 打印机ID
     */
    @TableId(type = IdType.AUTO)
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
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
