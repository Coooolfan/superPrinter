package com.coooolfan.superprinter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.coooolfan.superprinter.vo.PrintOrderCreateVO;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
     * 创建日yyyyMMdd（加快索引判断，冗余字段）
     */
    private Long createDay;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 从PrintOrderCreateVO创建PrintOrder实体
     * @param vo 订单创建VO
     * @param userId 用户ID
     * @param pageCount 文档页数
     * @return 新的打印订单实体
     */
    public static PrintOrder fromCreateVO(PrintOrderCreateVO vo, Long userId, Integer pageCount) {
        PrintOrder order = new PrintOrder();
        
        // 设置用户ID和文件IDs
        order.setUserId(userId);
        order.setFileIds(vo.getFileIds());
        
        // 设置打印机ID
        order.setPrinterId(vo.getPrinterId());
        
        // 设置打印参数
        order.setCopies(vo.getCopies());
        order.setPaperSize(vo.getPaperSize());
        order.setColorful(vo.getColor() ? 1 : 0);
        order.setDoubleSided(vo.getDuplex() ? 1 : 0);
        
        // 设置页数
        order.setPageCount(pageCount);
        order.setTotalPageCount(pageCount * vo.getCopies());
        
        // 计算订单金额 - 通过printerId是否为0判断是否为特惠打印机
        boolean isPrinterSpecial = vo.getPrinterId() != null && vo.getPrinterId() == 0;
        BigDecimal amount = calculateAmount(pageCount, vo.getCopies(), vo.getColor(), vo.getDuplex(), isPrinterSpecial);
        order.setAmount(amount);
        
        // 设置订单状态为已创建
        order.setStatus("CREATED");
        
        // 设置时间字段
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        // 设置createDay为yyyyMMdd格式
        order.setCreateDay(Long.parseLong(now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        order.setUpdateTime(now);
        
        // 版本号初始化为0
        order.setVersion(0);
        
        return order;
    }
    
    /**
     * 计算订单金额
     * 参考前端计算逻辑实现
     */
    private static BigDecimal calculateAmount(int pageCount, int copies, boolean isColor, boolean isDuplex, boolean isPrinterSpecial) {
        // 基础价格（元/页）
        double basePrice = isColor ? 0.5 : 0.2;
        
        // 双面打印折扣
        double duplexDiscount = isDuplex ? 0.9 : 1.0;
        
        // 特惠打印机折扣
        double printerDiscount = isPrinterSpecial ? 0.5 : 1.0;
        
        // 计算总价: 页数 * 份数 * 单价 * 双面打印折扣 * 打印机折扣
        double totalPrice = pageCount * copies * basePrice * duplexDiscount * printerDiscount;
        
        // 转换为BigDecimal并保留两位小数，使用RoundingMode替代废弃的int常量
        return BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);
    }
}