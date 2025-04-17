package com.coooolfan.superprinter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.PaperType;
import com.coooolfan.superprinter.mapper.PaperTypeMapper;
import com.coooolfan.superprinter.service.PaperTypeService;
import org.springframework.stereotype.Service;

/**
 * 纸张类型服务实现类
 */
@Service
public class PaperTypeServiceImpl extends ServiceImpl<PaperTypeMapper, PaperType> implements PaperTypeService {
}