// mbcFinalProject1 - com.mbc.fin1.dao - ParkingPredictionDao.java
package com.mbc.fin1.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mbc.fin1.dto.ParkingPredictionDto;

@Mapper
public interface ParkingPredictionDao {
	// 초단기/단기: 30분 단위 진료과별 예약 수 가져오기
    Map<String, Integer> getTargetTimeReservationStats(
        @Param("targetDate") LocalDate targetDate, 
        @Param("targetTime") LocalTime targetTime
    );
    Map<String, Integer> getDailyReservationStats(@Param("targetDate") LocalDate targetDate);	// 중기: 일 단위 진료과별 예약 수 가져오기
    
	void upsertPrediction(ParkingPredictionDto prediction);	// 주차 수요 예측 결과를 DB에 저장 (UPSERT)
	
	// 초단기/단기: 입차 그래프 데이터 가져오기
	List<Map<String, Object>> getParkingChartData(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
	// 중기: 입차 그래프 데이터 가져오기
    List<Map<String, Object>> getMidTermChartData(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
	
    int countPredictionsByDate(@Param("targetDate") LocalDate targetDate);					// 초단기/단기: 해당일 00:00~23:30 주차 수요 예측 데이터가 있는지 찾아보기
    int countMidPredictionsByDate(@Param("targetDate") LocalDate targetDate);				// 중기: 해당일 00:00~23:30 주차 수요 예측 데이터가 있는지 찾아보기
    int countPredictionsByDatetime(@Param("targetDatetime") LocalDateTime targetDatetime);	// 시간대가 이미 저장되어 있는지 확인
    
    void deletePredictionsByDate(java.time.LocalDate targetDate);	// 예약 변동 시 해당일 예측 삭제하기
    
    List<Map<String, Object>> getForecastTypesByDate(LocalDate targetDate);	// 중기->단기, 단기->초단기 로 예측을 덮어써야 하는지
}