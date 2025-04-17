package com.coooolfan.superprinter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.PrinterResource;
import com.coooolfan.superprinter.enums.PrinterStatus;
import com.coooolfan.superprinter.exception.BusinessException;
import com.coooolfan.superprinter.mapper.PrinterResourceMapper;
import com.coooolfan.superprinter.service.PrinterResourceService;
import com.coooolfan.superprinter.vo.response.MessageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 打印机资源服务实现类
 */
@Service
public class PrinterResourceServiceImpl extends ServiceImpl<PrinterResourceMapper, PrinterResource>
                implements PrinterResourceService {

        @Override
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
                printerResource.setCreateTime(LocalDateTime.now());
                printerResource.setUpdateTime(LocalDateTime.now());

                // 保存打印机资源
                boolean success = save(printerResource);
                if (!success) {
                        throw new BusinessException("添加打印机资源失败");
                }

                return printerResource;
        }

        @Override
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

        @Override
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

        @Override
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

        @Override
        public List<PrinterResource> getAllPrinters() {
                // 查询所有打印机资源
                LambdaQueryWrapper<PrinterResource> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.orderByDesc(PrinterResource::getUpdateTime);
                return list(queryWrapper);
        }
}