package com.coooolfan.superprinter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打印机资源实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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


    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 更新时间,用于刷新特惠打印机余额
     */
    private Long updateDay;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
