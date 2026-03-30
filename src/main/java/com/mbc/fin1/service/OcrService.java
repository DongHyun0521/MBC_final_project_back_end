// mbcFinalProject1 - com.mbc.fin1.service - OcrService.java
package com.mbc.fin1.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
// 💡 DB 저장을 안 하므로 @Transactional 삭제!
public class OcrService {

    // 💡 사진을 받아 파이썬 서버에 넘기고, 순수 텍스트 결과만 Map으로 리턴
    public Map<String, Object> readLicensePlate(MultipartFile file) {
        System.out.println("=> OcrService: readLicensePlate | "+ new Date());
        
        Map<String, Object> resultMap = new HashMap<>();
        List<String> debugImages = new ArrayList<>();

        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            debugImages.add(imageToBase64(original));
            resultMap.put("debugImages", debugImages); // 디버그용 이미지 담기

            Map<String, Object> aiResult = callPythonAiServer(file);
            
            if (aiResult != null && "success".equals(aiResult.get("status"))) {
                resultMap.put("status", "success");
                resultMap.put("plateNumber", aiResult.get("plate_number"));
                resultMap.put("country", aiResult.get("country"));
                resultMap.put("isEv", aiResult.get("is_ev"));
            } else {
                resultMap.put("status", "fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "error");
        }
        return resultMap;
    }

    // 파이썬 AI 서버 호출 (로직 동일)
    private Map<String, Object> callPythonAiServer(MultipartFile file) {
        System.out.println("=> OcrService: 파이썬 AI 서버(8001번 포트) 호출 시작!");
        RestTemplate restTemplate = new RestTemplate();
        String aiServerUrl = "http://localhost:8001/api/v1/ocr";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(aiServerUrl, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("🚨 파이썬 서버 통신 실패: " + e.getMessage());
            Map<String, Object> failMap = new HashMap<>();
            failMap.put("status", "error");
            return failMap;
        }
    }

    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }
}