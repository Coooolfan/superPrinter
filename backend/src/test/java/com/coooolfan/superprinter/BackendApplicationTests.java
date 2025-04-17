package com.coooolfan.superprinter;

import com.coooolfan.superprinter.util.Minio.MinioUtil;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.coooolfan.superprinter.util.Minio.MinioUtil.BUCKET_NAME;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private MinioUtil minioUtil;

    @Test
    void contextLoads() throws Exception {
        System.out.println(minioUtil.getObjectInfo(BUCKET_NAME, "台州文化魅力详解.pptx"));
    }

}
