package com.coooolfan.superprinter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/print-order")
@SaCheckLogin
public class PrintOrderController {
}

