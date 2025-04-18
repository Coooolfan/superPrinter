package com.coooolfan.superprinter.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoUploadPreSignVO {
    private String originalName;
    private String fileType;
}
