package com.coooolfan.superprinter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coooolfan.superprinter.entity.PaperType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 纸张类型Mapper接口
 */
@Mapper
public interface PaperTypeMapper extends BaseMapper<PaperType> {
}