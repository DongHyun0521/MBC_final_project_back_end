// mbcFinalProject1 - com.mbc.fin1.service - ParkingLogService.java
package com.mbc.fin1.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dao.ParkingLogDao;
import com.mbc.fin1.dao.ParkingSpotDao;
import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.dto.ParkingSpotDto;

@Service
@Transactional
public class ParkingLogService {

    @Autowired private ParkingLogDao parkingLogDao;
    @Autowired private MemDao memDao;
    @Autowired private ParkingSpotDao parkingSpotDao;

    // application.properties에서 주입 (프로젝트 루트/images)
    @Value("${app.upload.dir}")
    private String uploadDir;

    // 파이썬 서버 연동
    private final String PYTHON_URL = "http://localhost:8001/license-plates-recognition";

    // 차량 입차 시
    public ParkingLogDto processEntry(MultipartFile file) throws IOException {
        // 파일 바이트를 먼저 보관 (Python 전송 + 이미지 저장 양쪽에 사용)
        byte[] fileBytes = file.getBytes();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() { return file.getOriginalFilename(); }
        });

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(PYTHON_URL, request, Map.class);
        Map<String, Object> aiResult = response.getBody();

        if (aiResult == null || !"success".equals(aiResult.get("status"))) {
            throw new RuntimeException("AI 실패: " + (aiResult != null ? aiResult.get("message") : "응답없음"));
        }

        // DTO 조립
        ParkingLogDto log = new ParkingLogDto();
        log.setLicensePlateCountry((String) aiResult.get("license_plate_country"));
        log.setCountryAccuracy(((Number) aiResult.get("country_accuracy")).doubleValue());
        log.setOcrAccuracy(((Number) aiResult.get("ocr_accuracy")).doubleValue());
        log.setIsEvLicensePlate((Boolean) aiResult.get("is_ev_license_plate"));

        String rawNum = (String) aiResult.get("vehicle_num");
        log.setVehicleNum(rawNum != null ? rawNum.replace(" ", "") : null);

        // OCR 실패 → 이미지 저장 안 함, DB 저장 안 함
        String vNum = log.getVehicleNum();
        if (vNum == null || vNum.isEmpty() || "Unknown".equals(vNum) || "인식불가".equals(vNum)) {
            log.setVehicleNum("인식불가");
            return log;
        }

        // 중복 입차 → 이미지 저장 안 함, DB 저장 안 함
        ParkingLogDto existing = parkingLogDao.selectRecentEntryLog(log.getVehicleNum());
        if (existing != null) {
            existing.setParkingStatus("ALREADY_PARKED");
            return existing;
        }

        // ── 여기까지 통과해야만 이미지 저장 ──
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(uploadDir, "vehicle"));
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(uploadDir, "plates"));

        String vFilename = "car_" + UUID.randomUUID().toString().replace("-", "") + ".jpg";
        String pFilename = "plate_" + UUID.randomUUID().toString().replace("-", "") + ".jpg";

        java.nio.file.Files.write(java.nio.file.Paths.get(uploadDir, "vehicle", vFilename), fileBytes);

        String plateBase64 = (String) aiResult.get("plate_img_base64");
        byte[] plateBytes = java.util.Base64.getDecoder().decode(plateBase64);
        java.nio.file.Files.write(java.nio.file.Paths.get(uploadDir, "plates", pFilename), plateBytes);

        log.setVehicleImg("/images/vehicle/" + vFilename);
        log.setLicensePlateImg("/images/plates/" + pFilename);

        // 회원 차량 여부 체크
        log.setIsMember(memDao.checkMemberVehicle(log.getVehicleNum()) > 0);

        // DB 저장
        parkingLogDao.insertEntryLog(log);

        // DB에 저장된 값으로 재조회
        ParkingLogDto saved = parkingLogDao.findById(log.getParkingLogId().intValue());

        // 주차 자리 추천
        ParkingSpotDto spot = parkingSpotDao.findNearestAvailableSpot();
        if (spot != null) {
            char rowLetter = (char) ('A' + spot.getParkingRow() - 1);
            saved.setRecommendedSpot(
                "지하 " + spot.getParkingFloor() + "층 " + rowLetter + "-" + spot.getParkingColumn()
            );
        }
        return saved;
    }

    // 주차 로그 출력
    public List<ParkingLogDto> selectAllParkingLogs() {
        return parkingLogDao.selectParkingLogs(); 
    }

    // 주차 로그 수정
    public void updateLogByAdmin(Long parkingLogId, String newNum, String newCountry, Integer adminId) {
        ParkingLogDto dto = new ParkingLogDto();
        dto.setParkingLogId(parkingLogId);
        dto.setVehicleNum(newNum);
        dto.setLicensePlateCountry(newCountry);
        dto.setAdminStaffId(adminId);
        parkingLogDao.updateParkingLog(dto);
    }
    
    // 주차 로그 삭제
    public void deleteParkingLog(Long parkingLogId, Integer adminId) {
        ParkingLogDto dto = new ParkingLogDto();
        dto.setParkingLogId(parkingLogId);
        dto.setAdminStaffId(adminId);
        parkingLogDao.deleteParkingLog(dto);
    }
}