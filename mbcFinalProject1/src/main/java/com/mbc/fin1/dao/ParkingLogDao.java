// mbcFinalProject1 - com.mbc.mid.dao - ParkingLogDao.java
package com.mbc.fin1.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mbc.fin1.dto.ParkingLogDto;

@Mapper
@Repository
public interface ParkingLogDao {
	// 수정: String → ParkingLogDto
	void insertEntryLog(ParkingLogDto logDto);	// 차량 입차 시
	
    ParkingLogDto selectRecentEntryLog(String vehicleNum);	// 차량 출차 시, 최근부터 입차 기록 검색
    void updateExitLog(ParkingLogDto logDto); 				// 출차 기록 업데이트
    
    void updatePaymentStatus(Long id);	// 결제 여부 업데이트
    
    // 추가
    void deleteById(Long parkingLogId);				// 동시 중복 로그 제거 (선점 실패 시)
    ParkingLogDto findById(int parkingLogId);		// PK로 단건 조회
    int isMemberVehicle(String vehicleNum);			// 회원차량 여부 확인
    Long getMemberIdByVehicle(String vehicleNum);	// 차량번호로 회원 ID 조회
    // 추가 끝
}