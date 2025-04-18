package com.coooolfan.superprinter.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.FileInfo;
import com.coooolfan.superprinter.mapper.FileInfoMapper;
import com.coooolfan.superprinter.util.Minio.MinioUtil;
import com.coooolfan.superprinter.vo.FileInfoUploadPreSignVO;
import com.coooolfan.superprinter.vo.response.FileInfoUploadPreSignResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 文件信息服务实现类
 */
@Service
@AllArgsConstructor
public class FileInfoService extends ServiceImpl<FileInfoMapper, FileInfo> implements IService<FileInfo> {
    private final FileInfoMapper fileInfoMapper;
    private final MinioUtil minioUtil;

    public FileInfoUploadPreSignResponse getFileInfoUploadPreSign(FileInfoUploadPreSignVO vo) {
        String objectName = UUID.randomUUID().toString();
        // 拼接上vo.getFileType()的‘/’后的部分
        objectName += "." + List.of(vo.getFileType().split("/")).getLast();
        String preSignUrl;
        try {
            preSignUrl = minioUtil.getPresignedUploadUrl(objectName, 60 * 10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        FileInfo fileInfo = new FileInfo();

        fileInfo.setUserId(StpUtil.getLoginIdAsLong());
        fileInfo.setUploadTime(LocalDateTime.now());
        fileInfo.setStoredName(objectName);

        fileInfo.setOriginalName(vo.getOriginalName());
        fileInfo.setFileType(vo.getFileType());
        fileInfo.setFileSize(0L);
        fileInfo.setPageCount(-1);

        fileInfoMapper.insert(fileInfo);
        return new FileInfoUploadPreSignResponse(preSignUrl);
    }
}