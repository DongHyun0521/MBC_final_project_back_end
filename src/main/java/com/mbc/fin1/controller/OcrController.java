// mbcFinalProject1 - com.mbc.fin1.controller - OcrController.java
package com.mbc.fin1.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.fin1.dto.OcrResponse;
import com.mbc.fin1.service.OcrService;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    // 입차 차량 번호판에 OCR
    @PostMapping("/ocr/entry")
    public OcrResponse entryImage(MultipartFile file) {
    	System.out.println("=> OcrController: entryImage | "+ new Date());
        return ocrService.processEntryImage(file);
    }
    
    // 출차 차량 번호판에 OCR
    @PostMapping("/ocr/exit")
    public OcrResponse exitImage(MultipartFile file) {
    	System.out.println("=> OcrController: exitImage | "+ new Date());
        return ocrService.processExitImage(file);
    }
}