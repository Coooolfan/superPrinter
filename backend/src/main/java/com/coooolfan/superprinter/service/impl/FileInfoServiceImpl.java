package com.coooolfan.superprinter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.FileInfo;
import com.coooolfan.superprinter.mapper.FileInfoMapper;
import com.coooolfan.superprinter.service.FileInfoService;
import org.springframework.stereotype.Service;

/**
 * 文件信息服务实现类
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {
}