// mbcFinalProject1 - com.mbc.fin1.service - ReceiptService.java
package com.mbc.fin1.service;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dao.ParkingLogDao;
import com.mbc.fin1.dao.ParkingSpotDao;
import com.mbc.fin1.dao.ReceiptDao;
import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.dto.ReceiptDto;

@Service
@Transactional
public class ReceiptService {
	
    @Autowired
    private ReceiptDao receiptDao;
    
    @Autowired
    private ParkingLogDao parkingLogDao;
    
    @Autowired
    private MemDao memDao;
    
    @Autowired
    private ParkingSpotDao parkingSpotDao;

    // 결제 정보 저장 및 출차 처리
    public void processReceipt(ReceiptDto receiptDto) {
        System.out.println("=> ReceiptService: processReceipt | "+ new Date());
        
        // 💡 0. 총 결제 금액(amount) 확실하게 계산해서 세팅
        int pFee = (receiptDto.getParkingFee() != null) ? receiptDto.getParkingFee() : 0;
        int eFee = (receiptDto.getEvChargingFee() != null) ? receiptDto.getEvChargingFee() : 0;
        receiptDto.setAmount(pFee + eFee);

        // 1. 결제 내역(영수증) 저장
        receiptDao.insertReceipt(receiptDto);
        
        // (기존의 파킹 로그 결제 상태 업데이트는 DB 컬럼 삭제로 인해 제거됨!)
        
        // 2. 결제가 완료되었으므로 바로 출차 처리(exit_time 및 주차요금 업데이트)
        ParkingLogDto log = parkingLogDao.findById(receiptDto.getParkingLogId().intValue());
        
        if (log != null) {
            log.setExitTime(LocalDateTime.now());
            
            // 회원 여부 확인
            boolean isMember = (memDao.checkMemberVehicle(log.getVehicleNum()) > 0);
            log.setIsMember(isMember);
            
            // 💡 [추가] 파킹 로그에도 최종 정산된 주차 요금을 남겨줍니다!
            log.setParkingFee(pFee); 
            
            // 출차 시간, 회원 여부, 주차 요금 DB 업데이트
            parkingLogDao.updateExitLog(log);
            
            // 3. 주차 자리 비우기
            parkingSpotDao.freeSpotByLogId(log.getParkingLogId());
        }
    }
}