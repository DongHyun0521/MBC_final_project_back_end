package com.mbc.fin1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.mbc.fin1.dto.EvPredictionDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvPredictionService {

    private final RestTemplate restTemplate;

    @Value("${fastapi.base-url}")
    private String fastapiBaseUrl;

    public EvPredictionDto getPrediction(Long chargerId) {
        String url = fastapiBaseUrl + "/predict/db/" + chargerId;

        try {
            return restTemplate.getForObject(url, EvPredictionDto.class);
        } catch (RestClientException e) {
            throw new RuntimeException("FastAPI 호출 실패: chargerId=" + chargerId, e);
        }
    }
}