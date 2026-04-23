// mbcFinalProject1 - com.mbc.fin1.dao - ParkingSpotDao.java
package com.mbc.fin1.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.mbc.fin1.dto.ParkingSpotDto;

@Mapper
@Repository
public interface ParkingSpotDao {
    ParkingSpotDto findNearestAvailableSpot();		// 일반 차량: 입구로부터 맨해튼 거리 최단 주차 자리 (충전기 없는 자리)
    ParkingSpotDto findNearestAvailableEvSpot();	// 한국 전기차: 입구로부터 맨해튼 거리 최단 전기차 충전기 자리

    void allocateSpot(@Param("spotId") Integer spotId, @Param("parkingLogId") Long parkingLogId);	// 주차 자리 매핑하기
    void freeSpotByLogId(Long parkingLogId);														// 주차 자리 출차하기

    List<ParkingSpotDto> findAllSpots();	// 주차 자리 전체 출력하기
}