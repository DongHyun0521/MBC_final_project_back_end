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
    ParkingSpotDto findNearestAvailableSpot();	// 입구로부터 가장 가까운 주차 자리 찾기
    
    void allocateSpot(@Param("spotId") Integer spotId, @Param("parkingLogId") Long parkingLogId);	// 주차 자리 매핑하기
    void freeSpotByLogId(Long parkingLogId);														// 주차 자리 출차하기
    
    List<ParkingSpotDto> findAllSpots();	// 주차 자리 전체 출력하기
}