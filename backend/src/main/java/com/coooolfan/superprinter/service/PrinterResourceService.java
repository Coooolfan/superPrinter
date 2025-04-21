package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.PrinterResource;
import com.coooolfan.superprinter.enums.PrinterStatus;
import com.coooolfan.superprinter.exception.BusinessException;
import com.coooolfan.superprinter.mapper.PrinterResourceMapper;
import com.coooolfan.superprinter.util.DateUtil;
import com.coooolfan.superprinter.vo.response.MessageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 打印机资源服务实现类
 */
@Service
public class PrinterResourceService extends ServiceImpl<PrinterResourceMapper, PrinterResource> {

    @Transactional(rollbackFor = Exception.class)
    public PrinterResource addPrinter(PrinterResource printerResource) {
        // 参数校验
        if (printerResource == null) {
            throw new BusinessException("打印机资源信息不能为空");
        }
        if (printerResource.getPrinterName() == null || printerResource.getPrinterName().trim().isEmpty()) {
            throw new BusinessException("打印机名称不能为空");
        }
        if (printerResource.getPaperCount() == null || printerResource.getPaperCount() < 0) {
            throw new BusinessException("纸张数量不能为负数");
        }

        // 设置默认值
        printerResource.setStatus(PrinterStatus.ONLINE.getCode());
        printerResource.setVersion(0);
        printerResource.setUpdateDay(DateUtil.getCurrentYYYYMMDD());
        printerResource.setUpdateTime(LocalDateTime.now());

        // 保存打印机资源
        boolean success = save(printerResource);
        if (!success) {
            throw new BusinessException("添加打印机资源失败");
        }

        return printerResource;
    }

    @Transactional(rollbackFor = Exception.class)
    public MessageResponse updatePrinter(PrinterResource printerResource) {
        // 参数校验
        if (printerResource == null || printerResource.getPrinterId() == null) {
            throw new BusinessException("打印机资源信息不能为空");
        }
        if (printerResource.getPrinterName() != null && printerResource.getPrinterName().trim().isEmpty()) {
            throw new BusinessException("打印机名称不能为空");
        }
        if (printerResource.getPaperCount() != null && printerResource.getPaperCount() < 0) {
            throw new BusinessException("纸张数量不能为负数");
        }

        // 检查打印机是否存在
        PrinterResource existingPrinter = getById(printerResource.getPrinterId());
        if (existingPrinter == null) {
            throw new BusinessException("打印机资源不存在");
        }

        // 更新打印机资源
        printerResource.setUpdateTime(LocalDateTime.now());
        boolean success = updateById(printerResource);
        if (!success) {
            throw new BusinessException("更新打印机资源失败");
        }

        return new MessageResponse("更新打印机资源成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public MessageResponse deletePrinter(Long printerId) {
        // 参数校验
        if (printerId == null) {
            throw new BusinessException("打印机ID不能为空");
        }

        // 检查打印机是否存在
        PrinterResource existingPrinter = getById(printerId);
        if (existingPrinter == null) {
            throw new BusinessException("打印机资源不存在");
        }

        // 删除打印机资源
        boolean success = removeById(printerId);
        if (!success) {
            throw new BusinessException("删除打印机资源失败");
        }

        return new MessageResponse("删除打印机资源成功");
    }

    public PrinterResource getPrinter(Long printerId) {
        // 参数校验
        if (printerId == null) {
            throw new BusinessException("打印机ID不能为空");
        }

        // 查询打印机资源
        PrinterResource printerResource = getById(printerId);
        if (printerResource == null) {
            throw new BusinessException("打印机资源不存在");
        }

        return printerResource;
    }

    public List<PrinterResource> getAllPrinters() {
        // 使用（update_day的不等于条件）更新特惠打印机资源
        // UPDATE printer_resource SET paper_count=10 WHERE printer_id=0 AND update_day <> ？;
        // 这里的update_day是当前日,即DateUtil.getCurrentYYYYMMDD()的值，printer_id是特惠打印机的ID,即0
        // 幂等更新，如果请求量太大，也可以直接用定时任务更新，而不是每次查询更新

        Long currentYYYYMMDD = DateUtil.getCurrentYYYYMMDD();

        this.update()
                .set("paper_count", 10)
                .set("update_day", currentYYYYMMDD)
                .ne("update_day", currentYYYYMMDD)
                .eq("printer_id", 0)
                .update();

        // 查询所有打印机资源
        LambdaQueryWrapper<PrinterResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(PrinterResource::getUpdateTime);
        return list(queryWrapper);
    }
}