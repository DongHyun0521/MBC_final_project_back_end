// mbcFinalProject1 - com.mbc.fin1.controller - ParkingPredictionController.java
package com.mbc.fin1.controller;

import java.time.LocalDate;
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

    // 📈 [UI 왼쪽] 단기 예측: 날짜별 00:00~23:30 (30분 단위) 주차 흐름
    @GetMapping("/parkingChart/short")
    public List<Map<String, Object>> getShortTermChart(
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        // 안 보내면 오늘 ~ 3일 뒤까지 자동 세팅
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now().plusDays(2);
        
        return predictionService.getShortTermChartData(startDate, endDate);
    }

    // 📊 [UI 오른쪽] 중기 예측: 향후 3일~11일 뒤 (일 단위 피크 혼잡도)
    @GetMapping("/parkingChart/mid")
    public List<Map<String, Object>> getMidTermChart() {
        
        // 날짜 선택 불가능 기획 반영 -> 무조건 3일 뒤부터 7일(또는 11일) 뒤까지 자동 세팅
        LocalDate startDate = LocalDate.now().plusDays(3);
        LocalDate endDate = LocalDate.now().plusDays(9); // 총 7일치 막대그래프 
        
        return predictionService.getMidTermChartData(startDate, endDate);
    }
}