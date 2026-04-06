package com.mbc.fin1.controller;

import com.mbc.fin1.dto.EvPredictionDto;
import com.mbc.fin1.service.EvPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController  // ← REST API 컨트롤러
@RequiredArgsConstructor  // ← 생성자 자동 생성 (service 주입용)
public class EvPredictionController {

    // 서비스 주입
    private final EvPredictionService evPredictionService;

    @GetMapping("/api/ev/predict/{chargerId}")
    public EvPredictionDto getPrediction(
            @PathVariable("chargerId") Long chargerId
    ) {
        return evPredictionService.getPrediction(chargerId);
    }
}