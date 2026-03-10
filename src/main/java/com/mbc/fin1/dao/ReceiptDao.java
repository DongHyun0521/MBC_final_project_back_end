// mbcFinalProject1 - com.mbc.fin1.dao - ReceiptDao.java
package com.mbc.fin1.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mbc.fin1.dto.ReceiptDto;

@Mapper
@Repository
public interface ReceiptDao {
    void insertReceipt(ReceiptDto receiptDto);	// 영수증 저장
    int checkClinicVisit(String vehicleNum);	// 진료 완료 여부 확인 (당일 진료 완료 시 주차 2시간 무료)
}