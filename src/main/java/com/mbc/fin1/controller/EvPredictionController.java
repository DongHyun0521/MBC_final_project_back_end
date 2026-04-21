package com.mbc.fin1.controller;

import com.mbc.fin1.dto.EvPredictionDto;
import com.mbc.fin1.service.EvPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EvPredictionController {

    private final EvPredictionService evPredictionService;

    @GetMapping("/api/ev/predict/{chargerId}")
    public EvPredictionDto getPrediction(
            @PathVariable("chargerId") String chargerId
    ) {
        return evPredictionService.getPrediction(chargerId);
    }
}