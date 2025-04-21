package com.coooolfan.superprinter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.coooolfan.superprinter.service.PrintOrderService;
import com.coooolfan.superprinter.vo.PrintOrderCreateVO;
import com.coooolfan.superprinter.vo.response.PrintOrderCreateResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/print-order")
@SaCheckLogin
@AllArgsConstructor
public class PrintOrderController {

    private PrintOrderService printOrderService;

    @PostMapping
    public PrintOrderCreateResponse createOrder(@RequestBody PrintOrderCreateVO vo){
        return printOrderService.createOrder(vo);
    }
}

