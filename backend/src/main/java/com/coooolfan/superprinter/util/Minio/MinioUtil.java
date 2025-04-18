// 参考自 https://gitee.com/wangfugui-ma/minio-spring-boot-starter

package com.coooolfan.superprinter.util.Minio;

import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * MinioUtil工具类
 * 提供对MinIO对象存储服务的各种操作，包括存储桶和对象的创建、删除、复制和检索等功能
 */
@Component
@AllArgsConstructor
public class MinioUtil {
    /**
     * MinIO客户端实例
     */
    private MinioClient minioClient;

    /**
     * 默认存储桶名称
     */
    public static final String BUCKET_NAME = "superprinter";

    /**
     * 创建存储桶
     * 如果存储桶不存在，则创建新的存储桶
     * 
     * @param bucket 存储桶名称
     * @throws Exception 创建存储桶过程中可能发生的异常
     */
    public void createBucket(String bucket) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 上传文件到MinIO存储服务
     * 
     * @param stream     包含文件内容的输入流
     * @param bucket     目标存储桶名称
     * @param objectName 对象名称，即文件在MinIO中的路径
     * @throws Exception 上传过程中可能发生的异常
     */
    public void uploadFile(InputStream stream, String bucket, String objectName) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName)
                .stream(stream, -1, 10485760).build());
    }

    /**
     * 列出所有存储桶
     * 
     * @return 存储桶名称列表
     * @throws Exception 获取存储桶列表过程中可能发生的异常
     */
    public List<String> listBuckets() throws Exception {
        List<Bucket> list = minioClient.listBuckets();
        List<String> names = new ArrayList<>();
        list.forEach(b -> names.add(b.name()));
        return names;
    }

    /**
     * 列出指定存储桶中的所有文件
     * 
     * @param bucket 存储桶名称
     * @return 文件信息列表
     * @throws RuntimeException 列出文件过程中可能发生的异常
     */
    public List<Fileinfo> listFiles(String bucket) throws RuntimeException {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).recursive(true).build());

        List<Fileinfo> infos = new ArrayList<>();
        results.forEach(r -> {
            Fileinfo info = new Fileinfo();
            try {
                Item item = r.get();
                info.setFilename(item.objectName());
                info.setDirectory(item.isDir());
                infos.add(info);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return infos;
    }

    /**
     * 下载对象
     * 
     * @param bucket     存储桶名称
     * @param objectName 对象名称
     * @return 包含对象内容的输入流
     * @throws Exception 下载过程中可能发生的异常
     */
    public InputStream download(String bucket, String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(objectName).build());
    }

    /**
     * 删除存储桶
     * 
     * @param bucket 要删除的存储桶名称
     * @throws Exception 删除存储桶过程中可能发生的异常
     */
    public void deleteBucket(String bucket) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucket).build());
    }

    /**
     * 删除对象
     * 
     * @param bucket     存储桶名称
     * @param objectName 要删除的对象名称
     * @throws Exception 删除对象过程中可能发生的异常
     */
    public void deleteObject(String bucket, String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectName).build());
    }

    /**
     * 复制对象
     * 如果目标存储桶不存在，会先创建目标存储桶，然后复制对象
     * 
     * @param sourceBucket 源存储桶名称
     * @param sourceObject 源对象名称
     * @param targetBucket 目标存储桶名称
     * @param targetObject 目标对象名称
     * @throws Exception 复制对象过程中可能发生的异常
     */
    public void copyObject(String sourceBucket, String sourceObject, String targetBucket, String targetObject)
            throws Exception {
        this.createBucket(targetBucket);
        minioClient.copyObject(CopyObjectArgs.builder().bucket(targetBucket).object(targetObject)
                .source(CopySource.builder().bucket(sourceBucket).object(sourceObject).build()).build());
    }

    /**
     * 获取对象信息
     * 
     * @param bucket     存储桶名称
     * @param objectName 对象名称
     * @return 对象统计信息的字符串表示
     * @throws Exception 获取对象信息过程中可能发生的异常
     */
    public String getObjectInfo(String bucket, String objectName) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(objectName).build()).toString();
    }

    /**
     * 获取对象的预签名上传URL
     * 用于生成一个临时的URL，允许用户在指定的时间内上传文件到MinIO存储服务
     * 
     * @param objectName 对象名称
     * @param expires    URL的有效期（秒）
     * @return 预签名URL
     * @throws Exception 获取预签名URL过程中可能发生的异常
     */
    public String getPresignedUploadUrl(String objectName, Integer expires) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(BUCKET_NAME)
                .object(objectName)
                .expiry(expires, TimeUnit.SECONDS)
                .method(Method.PUT)
                .build());
    }

    /**
     * 列出所有存储桶中的所有文件
     * 
     * @return 所有文件的信息列表
     * @throws Exception 列出文件过程中可能发生的异常
     */
    public List<Fileinfo> listAllFile() throws Exception {
        List<String> list = this.listBuckets();
        List<Fileinfo> fileinfos = new ArrayList<>();
        for (String bucketName : list) {
            fileinfos.addAll(this.listFiles(bucketName));
        }

        return fileinfos;
    }

    /**
     * 获取对象的预签名下载URL
     * 与getPresignedObjectUrl类似，但过期时间单位为分钟
     * 
     * @param objectName 对象名称
     * @param expires    URL的有效期（分钟）
     * @return 预签名下载URL
     * @throws Exception 获取预签名下载URL过程中可能发生的异常
     */
    public String getPresignedDownloadUrl(String objectName, Integer expires) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(BUCKET_NAME)
                .object(objectName)
                .expiry(expires, TimeUnit.MINUTES)
                .method(Method.GET)
                .build());
    }
}
