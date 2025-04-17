package com.coooolfan.superprinter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coooolfan.superprinter.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单状态流转日志Mapper接口
 */
@Mapper
public interface OrderStatusLogMapper extends BaseMapper<OrderStatusLog> {
}