package com.coooolfan.superprinter.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.coooolfan.superprinter.vo.response.PrinterResourceResponse;
import jakarta.websocket.server.PathParam;

import com.coooolfan.superprinter.entity.PrinterResource;
import com.coooolfan.superprinter.service.PrinterResourceService;
import com.coooolfan.superprinter.vo.PrinterResourceVO;
import com.coooolfan.superprinter.vo.response.MessageResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 打印机资源控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/printer")
public class PrinterResourceController {
    private final PrinterResourceService printerResourceService;

    /**
     * 添加打印机资源
     *
     * @param printerResourceVO 打印机资源信息
     * @return 新创建的打印机资源
     */
    @PostMapping
    @SaCheckRole("MERCHANT")
    public ResponseEntity<PrinterResourceResponse> addPrinter(@RequestBody PrinterResourceVO printerResourceVO) {
        PrinterResource printerResource = new PrinterResource();
        BeanUtils.copyProperties(printerResourceVO, printerResource);
        PrinterResource savedPrinter = printerResourceService.addPrinter(printerResource);
        return ResponseEntity.ok(new PrinterResourceResponse(savedPrinter));
    }

    /**
     * 更新打印机资源
     *
     * @param printerResourceVO 打印机资源信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @SaCheckRole("MERCHANT")
    public ResponseEntity<MessageResponse> updatePrinter(
            @RequestBody PrinterResourceVO printerResourceVO,
            @PathVariable String id) {
        PrinterResource printerResource = new PrinterResource();
        BeanUtils.copyProperties(printerResourceVO, printerResource);
        printerResource.setPrinterId(Long.parseLong(id));
        return ResponseEntity.ok(printerResourceService.updatePrinter(printerResource));
    }

    /**
     * 删除打印机资源
     *
     * @param printerId 打印机ID
     * @return 操作结果
     */
    @DeleteMapping("/{printerId}")
    @SaCheckRole("MERCHANT")
    public ResponseEntity<MessageResponse> deletePrinter(@PathVariable Long printerId) {
        return ResponseEntity.ok(printerResourceService.deletePrinter(printerId));
    }

    /**
     * 获取打印机资源
     *
     * @param printerId 打印机ID
     * @return 打印机资源信息
     */
    @GetMapping("/{printerId}")
    public ResponseEntity<PrinterResourceResponse> getPrinter(@PathVariable Long printerId) {
        PrinterResource printerResource = printerResourceService.getPrinter(printerId);
        return ResponseEntity.ok(new PrinterResourceResponse(printerResource));
    }

    /**
     * 获取所有打印机资源列表
     *
     * @return 打印机资源列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<PrinterResourceResponse>> getAllPrinters() {
        List<PrinterResource> printers = printerResourceService.getAllPrinters();
        List<PrinterResourceResponse> printerResourceResponseList = printers.stream()
                .map(printerResource -> {
                    PrinterResourceResponse response = new PrinterResourceResponse();
                    BeanUtils.copyProperties(printerResource, response);
                    return response;
                }).toList();
        return ResponseEntity.ok(printerResourceResponseList);
    }
}