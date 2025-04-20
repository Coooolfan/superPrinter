package com.coooolfan.superprinter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.coooolfan.superprinter.service.PrintOrderService;
import com.coooolfan.superprinter.vo.OrderPreTokenVO;
import com.coooolfan.superprinter.vo.response.OrderPreBalanceResponse;
import com.coooolfan.superprinter.vo.response.OrderPreTokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/print-order-pre")
@SaCheckLogin
public class PrintOrderPreController {

    private final PrintOrderService printOrderService;

    @PostMapping
    public OrderPreTokenResponse getOrderPreToken(@RequestBody OrderPreTokenVO vo) {
        return printOrderService.getOrderPreToken(vo);
    }

    @GetMapping("/{uuid}")
    public OrderPreBalanceResponse getOrderPreBalance(@PathVariable String uuid) {
        return printOrderService.getOrderPreBalance(uuid);
    }
}
