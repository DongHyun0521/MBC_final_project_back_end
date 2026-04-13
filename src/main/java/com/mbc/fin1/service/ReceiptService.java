// mbcFinalProject1 - com.mbc.fin1.service - ReceiptService.java
package com.mbc.fin1.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.fin1.dao.ReceiptDao;
import com.mbc.fin1.dto.ReceiptDto;

@Service
@Transactional
public class ReceiptService {
    
    @Autowired
    private ReceiptDao receiptDao;
    
    @Autowired
    private ParkingLogService parkingLogService; // 💡 주차 반장님 소환!

    // 결제 정보 저장 및 출차 처리 지시
    public void processReceipt(ReceiptDto receiptDto) {
        System.out.println("=> ReceiptService: processReceipt | "+ new Date());
        
        // 1. 총 결제 금액 계산 및 셋팅
        int pFee = (receiptDto.getParkingFee() != null) ? receiptDto.getParkingFee() : 0;
        int eFee = (receiptDto.getEvChargingFee() != null) ? receiptDto.getEvChargingFee() : 0;
        receiptDto.setAmount(pFee + eFee);

        // 2. 결제 내역(영수증) DB 저장
        receiptDao.insertReceipt(receiptDto);
    }
}