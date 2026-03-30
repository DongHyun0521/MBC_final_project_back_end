// mbcFinalProject1 - com.mbc.fin1.dto - ParkingPredictionDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingPredictionDto {
    private Long parkingPredictionId;		// PK
    private LocalDateTime targetDatetime;	// 예측 대상 날짜+시간
    private LocalDateTime predictedTime;	// 예측 수행 시간
    private Integer predictedCars;			// 예측 주차 대수
    
    private String forecastType;	// 모델(vshort, short, mid)
    
    private Double temp;		// 기온
    private Double rainfallMm;	// 강수량
    private Double windSpeed;	// 풍속
    private Integer humidity;	// 습도
    private Double snowfallCm;	// 적설량
    
    private Double pm10;		// 미세먼지
    private Double pm25;		// 초미세먼지
    private Integer pm10Grade;	// 미세먼지 등급
    private Integer pm25Grade;	// 초미세먼지 등급
    
    private Boolean isHoliday;	// TRUE:주말/공휴일
    
    private Integer resInternal;		// 내과 예약 수
    private Integer resOrthopedics;		// 정형외과 예약 수
    private Integer resNeurosurgery;	// 신경외과 예약 수
    private Integer resPediatrics;		// 소아청소년과 예약 수
    private Integer resEnt;				// 이비인후과
    private Integer resDermatology;		// 피부과
    private Integer resOphthalmology;	// 안과
    private Integer resDentistry;		// 치과
    private Integer resPsychiatry;		// 정신건강의학과
    private Integer resTotal;			// 전체 예약 수
}