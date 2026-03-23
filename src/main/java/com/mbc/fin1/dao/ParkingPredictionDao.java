// mbcFinalProject1 - com.mbc.fin1.dao - ParkingPredictionDao.java
package com.mbc.fin1.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mbc.fin1.dto.ParkingPredictionDto;

@Mapper
public interface ParkingPredictionDao {
    Map<String, Integer> getTargetTimeReservationStats(
        @Param("targetDate") LocalDate targetDate, 
        @Param("targetTime") LocalTime targetTime
    );	// 30분 단위 진료과별 예약 수 가져오기
    Map<String, Integer> getDailyReservationStats(@Param("targetDate") LocalDate targetDate);	// 일 단위 진료과별 예약 수 가져오기
    
	void upsertPrediction(ParkingPredictionDto prediction);	// 주차 수요 예측 결과를 DB에 저장
	
	List<Map<String, Object>> getParkingChartData(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );	// 초단기/단기 입차 그래프 데이터 가져오기
    List<Map<String, Object>> getMidTermChartData(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );	// 중기 입차 그래프 데이터 가져오기
	
    int countPredictionsByDate(@Param("targetDate") LocalDate targetDate);	// 해당일 00:00~23:30 주차 수요 예측 데이터가 있는지 찾아보기
}