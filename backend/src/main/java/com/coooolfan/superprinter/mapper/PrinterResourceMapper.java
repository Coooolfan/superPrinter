package com.coooolfan.superprinter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coooolfan.superprinter.entity.PrinterResource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打印机资源Mapper接口
 */
@Mapper
public interface PrinterResourceMapper extends BaseMapper<PrinterResource> {
}