package com.coooolfan.superprinter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单状态流转日志实体类
 */
@Data
@TableName("order_status_log")
public class OrderStatusLog {
    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 原状态
     */
    private String fromStatus;

    /**
     * 目标状态
     */
    private String toStatus;

    /**
     * 操作人ID
     */
    private Long operator;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 备注
     */
    private String remark;
}