// mbcFinalProject1 - com.mbc.fin1.controller - ReceiptController.java
package com.mbc.fin1.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.fin1.dto.ReceiptDto;
import com.mbc.fin1.service.ReceiptService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    // 결제 정보(영수증) 저장
    @PostMapping("/receipt")
    public String pay(@RequestBody ReceiptDto receiptDto) {
        System.out.println("=> ReceiptController: pay | "+ new Date());
        try {
            receiptService.processReceipt(receiptDto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}