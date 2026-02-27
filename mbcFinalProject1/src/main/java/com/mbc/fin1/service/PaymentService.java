// middleProject - com.mbc.mid.service - OcrService.java
package com.mbc.fin1.service;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dao.ParkingLogDao;
import com.mbc.fin1.dao.PaymentDao;
import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.dto.PaymentDto;

@Service
@Transactional
public class PaymentService {
	
	@Autowired
    private PaymentDao paymentDao;
    
    @Autowired
    private ParkingLogDao parkingLogDao;
    
    @Autowired
    private MemDao memDao;

    // 결제 정보 저장
    public void processPayment(PaymentDto paymentDto) {
        System.out.println("=> PaymentService: processPayment | "+ new Date());
        
        // 1. 결제 내역 저장
        paymentDao.insertPayment(paymentDto);
        
        // 2. 파킹 로그 결제 상태 true 업데이트
        parkingLogDao.updatePaymentStatus(paymentDto.getParkingLogId());
        
        // 3. 결제가 완료되었으므로 바로 출차 처리(exit_time 업데이트)
        ParkingLogDto log = parkingLogDao.findById(paymentDto.getParkingLogId().intValue());
        if (log != null) {
            log.setExitTime(LocalDateTime.now());
            boolean isMember = (memDao.checkMemberVehicle(log.getVehicleNum()) > 0);
            log.setIsMember(isMember);
            parkingLogDao.updateExitLog(log);
        }
    }
}
