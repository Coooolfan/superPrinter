package com.coooolfan.superprinter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.OrderStatusLog;
import com.coooolfan.superprinter.mapper.OrderStatusLogMapper;
import com.coooolfan.superprinter.service.OrderStatusLogService;
import org.springframework.stereotype.Service;

/**
 * 订单状态流转日志服务实现类
 */
@Service
public class OrderStatusLogServiceImpl extends ServiceImpl<OrderStatusLogMapper, OrderStatusLog>
        implements OrderStatusLogService {
}