package com.mbc.fin1.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.mbc.fin1.dto.ParkingSpotDto;

@Mapper
@Repository
public interface ParkingSpotDao {
    // 가장 가까운 빈자리 1개 찾기
    ParkingSpotDto findNearestAvailableSpot();
    
    // 주차 자리 선점 (is_parked = true, FK 연결)
    void allocateSpot(@Param("spotId") Integer spotId, @Param("parkingLogId") Long parkingLogId);
    
    // 출차 시 주차 자리 반환 (is_parked = false, FK 해제)
    void freeSpotByLogId(Long parkingLogId);
}