package com.mbc.fin1.controller;

import com.mbc.fin1.service.NfcReaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nfc")
public class NfcController {

    // 다른 서비스 객체로 변경되지 않도록 상수로 고정
    private final NfcReaderService nfcReaderService;

    public NfcController(NfcReaderService nfcReaderService) {
        this.nfcReaderService = nfcReaderService;
    }

    @PostMapping("/read")
    public ResponseEntity<?> readCardUid() {
        try {
            // 카드 리더기가 UID를 반환할 때까지 대기
            String uid = nfcReaderService.waitForCardAndReadUid();

            Map<String, Object> result = new HashMap<>();
            
            result.put("is_success", true);
            result.put("card_uid", uid);

            // 200(OK)과 함께 result 객체 -> JSON 형태로 직렬화하여 반환
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            
            error.put("is_success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(500).body(error);
        }
    }
}