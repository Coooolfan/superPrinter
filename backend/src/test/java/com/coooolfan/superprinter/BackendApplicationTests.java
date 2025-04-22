package com.coooolfan.superprinter;

import com.coooolfan.superprinter.util.Minio.MinioUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private MinioUtil minioUtil;

    @Test
    void contextLoads() throws Exception {
//        System.out.println(minioUtil.getPresignedUploadUrl("test.txt", 6000));
//        System.out.println(minioUtil.listAllFile());
        System.out.println(minioUtil.getPresignedDownloadUrl("test.txt", 6000));
    }

}
