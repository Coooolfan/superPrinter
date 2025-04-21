package com.coooolfan.superprinter.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 获取当日的YYYYMMDD格式Long类型数据
     * 
     * @return 当日日期的Long类型表示
     */
    public static Long getCurrentYYYYMMDD() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Long.parseLong(today.format(formatter));
    }
}
