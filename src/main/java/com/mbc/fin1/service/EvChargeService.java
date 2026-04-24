// mbcFinalProject1 - com.mbc.fin1.service - EvChargeService.java
package com.mbc.fin1.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EvChargeService {

    // 파이썬 서버 주소 상수화
    private final String PYTHON_AI_URL = "http://localhost:8001/license-plates-recognition"; // 8001 OCR 엔진
    private final String PYTHON_BRAIN_URL = "http://localhost:8004/api/ev-charge";           // 8004 정산/EV 서버

    // 전기차 번호판 OCR 스캔 및 입차 여부 체크
    public Map<String, Object> processEvScan(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        RestTemplate restTemplate = new RestTemplate();
        
        // 파이썬 8001 (AI) 서버로 사진 전송 (OCR 요청)
        HttpHeaders multipartHeaders = new HttpHeaders();
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> ocrBody = new LinkedMultiValueMap<>();
        ocrBody.add("file", new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        
        HttpEntity<MultiValueMap<String, Object>> ocrReq = new HttpEntity<>(ocrBody, multipartHeaders);
        ResponseEntity<Map> ocrRes = restTemplate.postForEntity(PYTHON_AI_URL, ocrReq, Map.class);
        Map<String, Object> aiResult = ocrRes.getBody();

        String vehicleNum = (String) aiResult.get("vehicle_num");
        vehicleNum = (vehicleNum != null) ? vehicleNum.replace(" ", "") : "Unknown";

        // OCR 실패 시 바로 리턴
        if ("Unknown".equals(vehicleNum)) {
            Map<String, Object> failResult = new HashMap<>();
            failResult.put("is_success", false);
            failResult.put("message", "번호판을 인식할 수 없습니다.");
            return failResult;
        }

        // 파이썬 8004 (DB) 서버로 검증 요청
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> checkReqBody = new HashMap<>();
        checkReqBody.put("vehicle_num", vehicleNum);
        
        HttpEntity<Map<String, String>> checkReq = new HttpEntity<>(checkReqBody, jsonHeaders);
        
        try {
            // 파이썬 8004번의 `/api/ev-charge/check-entry` 호출
            ResponseEntity<Map> checkRes = restTemplate.postForEntity(PYTHON_BRAIN_URL + "/check-entry", checkReq, Map.class);
            return checkRes.getBody();

        } catch (HttpStatusCodeException e) {
            // 8004 서버에서 입차 기록이 없어서 에러를 뱉었을 경우
            Map<String, Object> failResult = new HashMap<>();
            failResult.put("is_success", false);
            failResult.put("vehicle_num", vehicleNum);
            failResult.put("message", "입차 기록을 찾을 수 없습니다.");
            return failResult;
        }
    }

    // 차량 번호 4자리 수동 검색 API
    public Map<String, Object> processManualSearch(String lastFourDigits) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> searchReqBody = new HashMap<>();
        searchReqBody.put("last_four_digits", lastFourDigits);
        
        HttpEntity<Map<String, String>> searchReq = new HttpEntity<>(searchReqBody, jsonHeaders);
        
        try {
            // 파이썬 8004번의 `/api/ev-charge/search` 호출
            ResponseEntity<Map> searchRes = restTemplate.postForEntity(PYTHON_BRAIN_URL + "/search", searchReq, Map.class);
            return searchRes.getBody();
            
        } catch (HttpStatusCodeException e) {
            Map<String, Object> failResult = new HashMap<>();
            failResult.put("is_success", false);
            failResult.put("message", "서버 검색 중 오류가 발생했습니다.");
            return failResult;
        }
    }
}