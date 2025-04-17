package com.coooolfan.superprinter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.PrintOrder;
import com.coooolfan.superprinter.mapper.PrintOrderMapper;
import com.coooolfan.superprinter.service.PrintOrderService;
import org.springframework.stereotype.Service;

/**
 * 打印订单服务实现类
 */
@Service
public class PrintOrderServiceImpl extends ServiceImpl<PrintOrderMapper, PrintOrder> implements PrintOrderService {
}