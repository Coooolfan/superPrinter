package com.coooolfan.superprinter.vo.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.coooolfan.superprinter.entity.PrintOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintOrderCreateResponse {
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
     * 创建时间
     */
    private LocalDateTime createTime;
}
