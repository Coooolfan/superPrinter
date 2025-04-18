package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.PrintOrder;
import com.coooolfan.superprinter.mapper.PrintOrderMapper;
import org.springframework.stereotype.Service;

/**
 * 打印订单服务实现类
 */
@Service
public class PrintOrderService extends ServiceImpl<PrintOrderMapper, PrintOrder> implements IService<PrintOrder> {
}