package com.coooolfan.superprinter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息实体类
 */
@Data
@TableName("file_info")
public class FileInfo {
    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long fileId;

    /**
     * 上传用户ID
     */
    private Long userId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * MinIO中的对象名
     */
    private String storedName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 页数（异步计算）
     */
    private Integer pageCount;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
}