// mbcFinalProject1 - com.mbc.fin1.dao - PaymentDao.java
package com.mbc.fin1.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mbc.fin1.dto.PaymentDto;

@Mapper
@Repository
public interface PaymentDao {
    void insertPayment(PaymentDto paymentDto);	// 결제 기록 저장
    
    int checkClinicVisit(String vehicleNum);	// 진료 완료 여부 확인 (당일 진료 완료 시 주차 2시간 무료)
}