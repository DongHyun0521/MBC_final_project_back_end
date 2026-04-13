// mbcFinalProject1 - com.mbc.fin1.dao - ParkingLogDao.java
package com.mbc.fin1.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mbc.fin1.dto.ParkingLogDto;

@Mapper
@Repository
public interface ParkingLogDao {
	void insertEntryLog(ParkingLogDto logDto);	// 차량 입차 시
	
    ParkingLogDto selectRecentEntryLog(String vehicleNum);	// 차량 출차 시, 최근부터 입차 기록 검색
    int updateExitLog(ParkingLogDto dto); 					// 출차 기록 업데이트
    
    List<ParkingLogDto> selectParkingLogs();	// 주차 로그 출력
    int updateParkingLog(ParkingLogDto dto);	// 주차 로그 수정
    int deleteParkingLog(ParkingLogDto dto);	// 주차 로그 삭제
    
    ParkingLogDto findById(int parkingLogId);	// PK로 단건 조회
}