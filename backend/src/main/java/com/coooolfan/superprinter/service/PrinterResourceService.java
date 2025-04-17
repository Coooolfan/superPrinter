package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coooolfan.superprinter.entity.PrinterResource;
import com.coooolfan.superprinter.vo.response.MessageResponse;

import java.util.List;

/**
 * 打印机资源服务接口
 */
public interface PrinterResourceService extends IService<PrinterResource> {

    /**
     * 添加打印机资源
     *
     * @param printerResource 打印机资源信息
     * @return 新创建的打印机资源
     */
    PrinterResource addPrinter(PrinterResource printerResource);

    /**
     * 更新打印机资源
     *
     * @param printerResource 打印机资源信息
     * @return 操作结果
     */
    MessageResponse updatePrinter(PrinterResource printerResource);

    /**
     * 删除打印机资源
     *
     * @param printerId 打印机ID
     * @return 操作结果
     */
    MessageResponse deletePrinter(Long printerId);

    /**
     * 获取打印机资源
     *
     * @param printerId 打印机ID
     * @return 打印机资源信息
     */
    PrinterResource getPrinter(Long printerId);

    /**
     * 获取所有打印机资源列表
     *
     * @return 打印机资源列表
     */
    List<PrinterResource> getAllPrinters();
}