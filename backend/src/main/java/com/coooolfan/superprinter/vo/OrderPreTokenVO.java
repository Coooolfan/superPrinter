package com.coooolfan.superprinter.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderPreTokenVO {
    // 此订单的文件列表 以逗号分隔
    private String fileIds;
}
