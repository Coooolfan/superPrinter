package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.PaperType;
import com.coooolfan.superprinter.mapper.PaperTypeMapper;
import org.springframework.stereotype.Service;

/**
 * 纸张类型服务实现类
 */
@Service
public class PaperTypeService extends ServiceImpl<PaperTypeMapper, PaperType> implements IService<PaperType> {
}