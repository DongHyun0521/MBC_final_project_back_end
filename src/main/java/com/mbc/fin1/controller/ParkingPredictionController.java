// mbcFinalProject1 - com.mbc.fin1.controller - ParkingPredictionController.java
package com.mbc.fin1.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.fin1.service.ParkingPredictionService;

@RestController
@RequestMapping("/dashBoard")
public class ParkingPredictionController {

    @Autowired
    private ParkingPredictionService predictionService;

    // 초단기/단기: 시작일~종료일 30분 단위 입차 예측 
    @GetMapping("/parkingChart/short")
    public List<Map<String, Object>> getShortTermChart(
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
    	System.out.println("=> ParkingPredictionController: getShortTermChart | " + new Date());
        
        if (startDate == null) startDate = LocalDate.now();	// 시작일 기본값: 오늘
        if (endDate == null) endDate = LocalDate.now();		// 종료일 기본값: 오늘
        
        return predictionService.getShortTermChartData(startDate, endDate);
    }

    // 중기: 3일~10일 후 일 단위 입차 예측
    @GetMapping("/parkingChart/mid")
    public List<Map<String, Object>> getMidTermChart() {
    	System.out.println("=> ParkingPredictionController: getMidTermChart | " + new Date());
    	
        LocalDate startDate = LocalDate.now().plusDays(3);	// 시작일 기본값: 3일 후 (변경 불가능)
        LocalDate endDate = LocalDate.now().plusDays(10);	// 종료일 기본값: 10일 후 (변경 불가능)
        
        return predictionService.getMidTermChartData(startDate, endDate);
    }
    
    // 현재 날씨 조회
    @GetMapping("/currentWeather")
    public Map<String, Object> getCurrentWeather() {
        System.out.println("=> ParkingPredictionController: getCurrentWeather | " + new Date());
        return predictionService.getCurrentWeather();
    }
}