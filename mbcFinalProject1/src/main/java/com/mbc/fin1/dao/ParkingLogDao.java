// mbcFinalProject1 - com.mbc.mid.dao - ParkingLogDao.java
package com.mbc.fin1.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mbc.fin1.dto.ParkingLogDto;

@Mapper
@Repository
public interface ParkingLogDao {
	void insertEntryLog(ParkingLogDto logDto);	// 차량 입차 시
	
    ParkingLogDto selectRecentEntryLog(String vehicleNum);	// 차량 출차 시, 최근부터 입차 기록 검색
    void updateExitLog(ParkingLogDto logDto); 				// 출차 기록 업데이트
    
    void updatePaymentStatus(Long id);	// 결제 여부 업데이트
    
    ParkingLogDto findById(int parkingLogId);	// PK로 단건 조회
}