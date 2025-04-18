package com.coooolfan.superprinter.util.Minio;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinioObject {
    String filename;

    Boolean directory;

    Long size;
}