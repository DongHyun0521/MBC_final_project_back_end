// mbcFinalProject1 - com.mbc.fin1.controller - EvChargeController.java
package com.mbc.fin1.controller;

import com.mbc.fin1.service.EvChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/ev-charge")
public class EvChargeController {

    @Autowired
    private EvChargeService evChargeService;

    // 전기차 번호판 사진 전송 및 OCR 인식
    @PostMapping("/scan")
    public ResponseEntity<?> scanEvPlate(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = evChargeService.processEvScan(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("is_success", false, "message", "스캔 처리 중 서버 오류 발생"));
        }
    }

    // 차량 번호 4자리 수동 검색
    @PostMapping("/search")
    public ResponseEntity<?> searchManualPlate(@RequestBody Map<String, String> requestData) {
        try {
            String lastFourDigits = requestData.get("last_four_digits");
            Map<String, Object> result = evChargeService.processManualSearch(lastFourDigits);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("is_success", false, "message", "검색 처리 중 서버 오류 발생"));
        }
    }
}