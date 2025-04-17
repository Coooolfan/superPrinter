package com.coooolfan.superprinter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coooolfan.superprinter.entity.PrintOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打印订单Mapper接口
 */
@Mapper
public interface PrintOrderMapper extends BaseMapper<PrintOrder> {
}