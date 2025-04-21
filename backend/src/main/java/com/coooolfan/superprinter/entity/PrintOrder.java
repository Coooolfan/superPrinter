package com.coooolfan.superprinter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 打印订单实体类
 */
@Data
@TableName("print_order")
public class PrintOrder {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件数组
     * 逗号分隔的文件ID列表
     */
    private String fileIds;

    /**
     * 打印机ID
     */
    private Long printerId;

    /**
     * 打印份数
     */
    private Integer copies;

    /**
     * 纸张类型
     */
    private String paperSize;

    /**
     * 是否彩色打印：0-黑白，1-彩色
     */
    private Integer colorful;

    /**
     * 是否双面打印：0-单面，1-双面
     */
    private Integer doubleSided;

    /**
     * 订单状态：CREATED,PAID,PROCESSING,READY_FOR_PICKUP,COMPLETED,CANCELLED
     */
    private String status;

    /**
     * 单份纸张数量
     */
    private Integer pageCount;

    /**
     * 订单总页数
     */
    private Integer totalPageCount;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 取件码
     */
    private String pickupCode;

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
     * 创建日
     *
     */
    private LocalDateTime updateTime;
}