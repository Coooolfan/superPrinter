package com.coooolfan.superprinter.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintOrderCreateVO {

    private Long printerId; // 打印机ID

    private String fileIds; // 上传的文件IDs

    private String paperSize; // 纸张大小

    private Boolean color; // 是否彩色打印

    private Integer copies; // 打印份数

    private Boolean duplex; // 是否双面打印

    private String remark; // 备注信息

    private String token; // 预处理token

}
