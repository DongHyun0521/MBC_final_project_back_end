// mbcFinalProject1 - com.mbc.fin1.controller - OcrController.java
package com.mbc.fin1.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.fin1.dto.OcrDto;
import com.mbc.fin1.service.OcrService;
import com.mbc.fin1.service.ParkingService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class OcrController {

    @Autowired
    private OcrService ocrService;
    
    @Autowired
    private ParkingService parkingService; // 💡 주차 반장님 추가!

    // 입차 차량 번호판에 OCR
    @PostMapping("/ocr/entry")
    public OcrDto entryImage(MultipartFile file) {
        System.out.println("=> OcrController: entryImage | "+ new Date());
        
        // 1. 순수하게 사진만 판독 (번호판, 국가, 전기차여부 등)
        Map<String, Object> ocrData = ocrService.readLicensePlate(file);
        
        if (!"success".equals(ocrData.get("status"))) {
            return new OcrDto("인식실패", "인식실패", null, "UNKNOWN", false, null, null, false, -1, null, null, null);
        }

        // 2. 판독된 데이터를 주차 반장에게 넘겨서 입차 처리
        return parkingService.processEntry(ocrData);
    }
    
    // 출차 차량 번호판에 OCR
    @PostMapping("/ocr/exit")
    public OcrDto exitImage(MultipartFile file) {
        System.out.println("=> OcrController: exitImage | "+ new Date());
        
        // 1. 순수하게 사진만 판독
        Map<String, Object> ocrData = ocrService.readLicensePlate(file);
        
        if (!"success".equals(ocrData.get("status"))) {
            return new OcrDto("인식실패", "인식실패", null, "UNKNOWN", false, null, null, false, -1, null, null, null);
        }

        // 2. 판독된 데이터를 주차 반장에게 넘겨서 요금 계산 및 출차 처리
        return parkingService.processExit(ocrData);
    }
}