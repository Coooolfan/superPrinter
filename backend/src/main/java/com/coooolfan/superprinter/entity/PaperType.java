package com.coooolfan.superprinter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 纸张类型实体类
 */
@Data
@TableName("paper_type")
public class PaperType {
    /**
     * 类型ID
     */
    @TableId(type = IdType.AUTO)
    private Long typeId;

    /**
     * 纸张类型名称
     */
    private String typeName;

    /**
     * 每页单价
     */
    private BigDecimal pricePerPage;

    /**
     * 是否可用：0-不可用，1-可用
     */
    private Integer isAvailable;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}