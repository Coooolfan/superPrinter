package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.OrderStatusLog;
import com.coooolfan.superprinter.mapper.OrderStatusLogMapper;
import org.springframework.stereotype.Service;

/**
 * 订单状态流转日志服务实现类
 */
@Service
public class OrderStatusLogService extends ServiceImpl<OrderStatusLogMapper, OrderStatusLog>
        implements IService<OrderStatusLog> {
}