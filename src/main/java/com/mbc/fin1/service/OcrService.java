// middleProject - com.mbc.fin1.service - OcrService.java
package com.mbc.fin1.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.mbc.fin1.dao.ReceiptDao;
import com.mbc.fin1.dto.OcrResponse;
import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.dto.ParkingSpotDto;

@Service
@Transactional
public class OcrService {

    @Autowired
    private ParkingLogDao parkingLogDao;
    
    @Autowired
    private MemDao memDao;
    
    @Autowired
    private ReceiptDao receiptDao;
    
    @Autowired
    private ParkingSpotDao parkingSpotDao;

    private static final Map<String, Object> ENTRY_LOCK = new ConcurrentHashMap<>();

    public OcrResponse processEntryImage(MultipartFile file) {
        System.out.println("=> OcrService: processEntryImage | "+ new Date());
        return processImageCommon(file, "ENTRY");
    }

    public OcrResponse processExitImage(MultipartFile file) {
        System.out.println("=> OcrService: processExitImage | "+ new Date());
        return processImageCommon(file, "EXIT");
    }

    // 파이썬 AI 서버 호출
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
            Map<String, Object> result = response.getBody();
            
            if (result != null && "success".equals(result.get("status"))) {
                return result; 
            }
        } catch (Exception e) {
            System.err.println("🚨 파이썬 서버 통신 실패: " + e.getMessage());
        }
        
        Map<String, Object> failMap = new HashMap<>();
        failMap.put("plate_number", "인식실패");
        failMap.put("country", "UNKNOWN");
        failMap.put("is_ev", false);
        return failMap;
    }

    private OcrResponse processImageCommon(MultipartFile file, String type) {
        System.out.println("=> OcrService: processImageCommon | "+ new Date());
        List<String> debugImages = new ArrayList<>();

        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            debugImages.add(imageToBase64(original));

            Map<String, Object> aiResult = callPythonAiServer(file);
            String finalResult = (String) aiResult.get("plate_number");
            String country = (String) aiResult.get("country");
            Boolean isEv = (Boolean) aiResult.get("is_ev");

            System.out.println("🤖 파이썬 인식 결과 -> 번호: " + finalResult + ", 국가: " + country + ", 전기차: " + isEv);
            
            String entryTimeStr = "";
            String exitTimeStr = "";
            Integer parkingFee = -1;
            Boolean isMember = false;

            if (isValidResult(finalResult)) {
                // ============================== [ 입차 로직 ] ==============================
                if (type.equals("ENTRY")) {
                    Object lock = ENTRY_LOCK.computeIfAbsent(finalResult, k -> new Object());
                    synchronized (lock) {
                        ParkingLogDto existingLog = parkingLogDao.selectRecentEntryLog(finalResult);

                        if (existingLog != null && existingLog.getExitTime() == null) {
                            // 💡 OcrResponse 생성자 순서 변경: country, isEv가 중간으로 이동!
                            return new OcrResponse(finalResult, "이미 주차장에 입차된 차량입니다.", debugImages, 
                                    country, isEv, "ALREADY_PARKED", null, isMember, 0, existingLog.getParkingLogId(), null, null);
                        }

                        ParkingLogDto newLog = new ParkingLogDto();
                        newLog.setVehicleNum(finalResult);
                        newLog.setLicensePlateCountry(country);
                        newLog.setIsEvLicensePlate(isEv);
                        
                        boolean isMem = (memDao.checkMemberVehicle(finalResult) > 0);
                        newLog.setIsMember(isMem);
                        parkingLogDao.insertEntryLog(newLog);
                        
                        // 전기차 맞춤형 주차 자리 배정 로직
                        ParkingSpotDto spot = null;
                        if (isEv != null && isEv) {
                            spot = parkingSpotDao.findNearestAvailableEvSpot();
                        }
                        
                        if (spot == null) {
                            spot = parkingSpotDao.findNearestAvailableSpot();
                        }

                        String allocatedSpotStr = "만차 (빈자리 없음)";

                        if (spot != null) {
                            parkingSpotDao.allocateSpot(spot.getSpotId(), newLog.getParkingLogId());
                            char rowChar = (char) ('A' + spot.getParkingRow() - 1);
                            allocatedSpotStr = "지하 " + spot.getParkingFloor() + "층: " + rowChar + "-" + spot.getParkingColumn();
                            
                            if (isEv != null && isEv && spot.getParkingFloor() == 1) {
                                allocatedSpotStr += " ⚡ (전기차 전용 구역)";
                            }
                        }

                        entryTimeStr = formatDateTime(LocalDateTime.now());
                        // 💡 OcrResponse 생성자 순서 변경
                        return new OcrResponse(finalResult, finalResult, debugImages, country, isEv, entryTimeStr, null, isMem, 0, newLog.getParkingLogId(), null, allocatedSpotStr);
                    }
                }
                
                // ============================== [ 출차 로직 ] ==============================
                else if (type.equals("EXIT")) {
                    ParkingLogDto log = parkingLogDao.selectRecentEntryLog(finalResult);
                    if (log == null) {
                        // 💡 OcrResponse 생성자 순서 변경
                        return new OcrResponse(finalResult, "입차 기록이 없습니다.", debugImages, country, isEv, null, null, false, 0, null, null, null);
                    }

                    Long memId = null;
                    isMember = (memDao.checkMemberVehicle(finalResult) > 0);
                    if (isMember) {
                        memId = memDao.getMemIdByVehicle(finalResult);
                    }

                    boolean hasClinicVisit = (receiptDao.checkClinicVisit(finalResult) > 0);
                    LocalDateTime now = LocalDateTime.now();
                    long totalMin = Duration.between(log.getEntryTime(), now).toMinutes();
                    
                    int feeToPay = calculateFee(totalMin, isMember, hasClinicVisit);

                    StringBuilder msgBuilder = new StringBuilder();
                    if (hasClinicVisit) msgBuilder.append("[진료할인 적용] ");
                    else if (isMember) msgBuilder.append("[회원할인 적용] ");

                    if (feeToPay == 0) {
                        log.setExitTime(now);
                        log.setIsMember(isMember);
                        log.setParkingFee(0); 
                        parkingLogDao.updateExitLog(log);
                        parkingSpotDao.freeSpotByLogId(log.getParkingLogId());
                        
                        msgBuilder.append("무료 주차/정산 완료. 안녕히 가십시오.");
                        // 💡 OcrResponse 생성자 순서 변경
                        return new OcrResponse(finalResult, msgBuilder.toString(), debugImages, 
                                country, isEv, formatDateTime(log.getEntryTime()), formatDateTime(now), 
                                isMember, 0, log.getParkingLogId(), memId, null);
                    } 
                    else {
                        msgBuilder.append("결제가 필요합니다.");
                        // 💡 OcrResponse 생성자 순서 변경
                        return new OcrResponse(finalResult, msgBuilder.toString(), debugImages,
                                country, isEv, formatDateTime(log.getEntryTime()), formatDateTime(now), 
                                isMember, feeToPay, log.getParkingLogId(), memId, null);
                    }
                }
            }
            // 💡 OcrResponse 생성자 순서 변경
            return new OcrResponse(finalResult, finalResult, debugImages, country, isEv, entryTimeStr, exitTimeStr, isMember, parkingFee, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            // 💡 OcrResponse 생성자 순서 변경 (에러 시 기본값 세팅)
            return new OcrResponse("에러", "에러", debugImages, "UNKNOWN", false, "에러", "에러", false, -1, null, null, null);
        }
    }

    private int calculateFee(long minutes, boolean isMember, boolean hasClinicVisit) {
        int freeMinutes = 30;
        if (hasClinicVisit) freeMinutes += 120;
        if (minutes <= freeMinutes) return 0;

        long chargeMinutes = minutes - freeMinutes;
        int unit = (int) Math.ceil(chargeMinutes / 30.0);
        int rate = isMember ? 1000 : 2000;
        int totalAmount = unit * rate;

        long days = (minutes / (24 * 60)) + 1;
        int dailyLimit = isMember ? 15000 : 30000;
        return Math.min(totalAmount, (int)(days * dailyLimit));
    }

    private boolean isValidResult(String text) {
        return text != null && !text.equals("인식실패") && !text.contains("에러") && !text.trim().isEmpty();
    }

    private String formatDateTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }
}

// ====================================================================================================

/*package com.mbc.fin1.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.mbc.fin1.dao.PaymentDao;
import com.mbc.fin1.dto.OcrResponse;
import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.dto.ParkingSpotDto;

@Service
@Transactional
public class OcrService {

    @Autowired
    private ParkingLogDao parkingLogDao;
    
    @Autowired
    private MemDao memDao;
    
    @Autowired
    private PaymentDao paymentDao;
    
    @Autowired
    private ParkingSpotDao parkingSpotDao;

    private static final Map<String, Object> ENTRY_LOCK = new ConcurrentHashMap<>();

    public OcrResponse processEntryImage(MultipartFile file) {
        System.out.println("=> OcrService: processEntryImage | "+ new Date());
        return processImageCommon(file, "ENTRY");
    }

    public OcrResponse processExitImage(MultipartFile file) {
        System.out.println("=> OcrService: processExitImage | "+ new Date());
        return processImageCommon(file, "EXIT");
    }

    // 💡 [핵심 추가] 파이썬 AI 서버로 사진을 보내고 결과를 받아오는 통신 메서드
    private String callPythonAiServer(MultipartFile file) {
        System.out.println("=> OcrService: 파이썬 AI 서버(8001번 포트) 호출 시작!");
        RestTemplate restTemplate = new RestTemplate();
        String aiServerUrl = "http://localhost:8001/api/v1/ocr"; // 파이썬 서버 주소

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // 파이썬 서버로 POST 요청 쏘기!
            ResponseEntity<Map> response = restTemplate.postForEntity(aiServerUrl, requestEntity, Map.class);
            Map<String, Object> result = response.getBody();
            
            // 파이썬이 돌려준 JSON 확인 후 번호판 텍스트만 쏙 뽑아오기
            if (result != null && "success".equals(result.get("status"))) {
                return (String) result.get("plate_number");
            }
        } catch (Exception e) {
            System.err.println("🚨 파이썬 서버 통신 실패: " + e.getMessage());
        }
        return "인식실패";
    }

    private OcrResponse processImageCommon(MultipartFile file, String type) {
        System.out.println("=> OcrService: processImageCommon | "+ new Date());
        List<String> debugImages = new ArrayList<>();

        try {
            // 프론트엔드 디버깅용 원본 이미지 저장
            BufferedImage original = ImageIO.read(file.getInputStream());
            debugImages.add(imageToBase64(original));

            // 💡 [코드 대폭 축소] 기존의 복잡했던 이미지 전처리와 Tess4J 로직을 다 날리고, 단 한 줄로 끝냅니다!
            String finalResult = callPythonAiServer(file);
            System.out.println("🤖 파이썬이 읽어준 번호판: " + finalResult);
            
            String entryTimeStr = "";
            String exitTimeStr = "";
            Integer parkingFee = -1;
            Boolean isMember = false;

            // --- 이 아래부터는 팀장님이 작성하신 완벽한 비즈니스(DB) 로직 그대로입니다! ---
            if (isValidResult(finalResult)) {
                // 입차 시
                if (type.equals("ENTRY")) {
                    Object lock = ENTRY_LOCK.computeIfAbsent(finalResult, k -> new Object());
                    synchronized (lock) {
                        ParkingLogDto existingLog = parkingLogDao.selectRecentEntryLog(finalResult);

                        if (existingLog != null && existingLog.getExitTime() == null) {
                            return new OcrResponse(finalResult, "이미 주차장에 입차된 차량입니다.", debugImages, 
                                    "ALREADY_PARKED", null, isMember, 0, existingLog.getParkingLogId(), null, null);
                        }

                        ParkingLogDto newLog = new ParkingLogDto();
                        newLog.setVehicleNum(finalResult);
                        boolean isMem = (memDao.checkMemberVehicle(finalResult) > 0);
                        newLog.setIsMember(isMem);
                        parkingLogDao.insertEntryLog(newLog);
                        
                        ParkingSpotDto spot = parkingSpotDao.findNearestAvailableSpot();
                        String allocatedSpotStr = "만차 (빈자리 없음)";

                        if (spot != null) {
                            parkingSpotDao.allocateSpot(spot.getSpotId(), newLog.getParkingLogId());
                            char rowChar = (char) ('A' + spot.getParkingRow() - 1);
                            allocatedSpotStr = "지하 " + spot.getParkingFloor() + "층: " + rowChar + "-" + spot.getParkingColumn();
                        }

                        entryTimeStr = formatDateTime(LocalDateTime.now());
                        return new OcrResponse(finalResult, finalResult, debugImages, entryTimeStr, null, isMem, 0, newLog.getParkingLogId(), null, allocatedSpotStr);
                    }
                }
                
                // 출차 시
                else if (type.equals("EXIT")) {
                    ParkingLogDto log = parkingLogDao.selectRecentEntryLog(finalResult);
                    if (log == null) {
                        return new OcrResponse(finalResult, "입차 기록이 없습니다.", debugImages, null, null, false, 0, null, null, null);
                    }

                    Long memId = null;
                    isMember = (memDao.checkMemberVehicle(finalResult) > 0);
                    if (isMember) {
                        memId = memDao.getMemIdByVehicle(finalResult);
                    }

                    boolean hasClinicVisit = (paymentDao.checkClinicVisit(finalResult) > 0);
                    LocalDateTime now = LocalDateTime.now();
                    long totalMin = Duration.between(log.getEntryTime(), now).toMinutes();
                    
                    int feeToPay = calculateFee(totalMin, isMember, hasClinicVisit);

                    if (log.getPaymentStatus() != null && log.getPaymentStatus()) {
                        feeToPay = 0; 
                    }

                    StringBuilder msgBuilder = new StringBuilder();
                    if (hasClinicVisit) msgBuilder.append("[진료할인 적용] ");
                    else if (isMember) msgBuilder.append("[회원할인 적용] ");

                    if (feeToPay == 0) {
                        log.setExitTime(now);
                        log.setIsMember(isMember);
                        parkingLogDao.updateExitLog(log);
                        parkingSpotDao.freeSpotByLogId(log.getParkingLogId());
                        
                        msgBuilder.append("무료 주차/정산 완료. 안녕히 가십시오.");
                        return new OcrResponse(finalResult, msgBuilder.toString(), debugImages, 
                                formatDateTime(log.getEntryTime()), formatDateTime(now), 
                                isMember, 0, log.getParkingLogId(), memId, null);
                    } 
                    else {
                        msgBuilder.append("결제가 필요합니다.");
                        return new OcrResponse(finalResult, msgBuilder.toString(), debugImages,
                                formatDateTime(log.getEntryTime()), formatDateTime(now), 
                                isMember, feeToPay, log.getParkingLogId(), memId, null);
                    }
                }
            }
            return new OcrResponse(finalResult, finalResult, debugImages, entryTimeStr, exitTimeStr, isMember, parkingFee, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new OcrResponse("에러", "에러", debugImages, "에러", "에러", false, -1, null, null, null);
        }
    }

    private int calculateFee(long minutes, boolean isMember, boolean hasClinicVisit) {
        int freeMinutes = 30;
        if (hasClinicVisit) freeMinutes += 120;
        if (minutes <= freeMinutes) return 0;

        long chargeMinutes = minutes - freeMinutes;
        int unit = (int) Math.ceil(chargeMinutes / 30.0);
        int rate = isMember ? 1000 : 2000;
        int totalAmount = unit * rate;

        long days = (minutes / (24 * 60)) + 1;
        int dailyLimit = isMember ? 15000 : 30000;
        return Math.min(totalAmount, (int)(days * dailyLimit));
    }

    private boolean isValidResult(String text) {
        return text != null && !text.equals("인식실패") && !text.contains("에러") && !text.trim().isEmpty();
    }

    private String formatDateTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }
}*/

// ====================================================================================================

/*package com.mbc.fin1.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dao.ParkingLogDao;
import com.mbc.fin1.dao.ParkingSpotDao;
import com.mbc.fin1.dao.PaymentDao;
import com.mbc.fin1.dto.OcrResponse;
import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.dto.ParkingSpotDto;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

@Service
@Transactional
public class OcrService {

    @Autowired
    private ParkingLogDao parkingLogDao;
    
    @Autowired
    private MemDao memDao;
    
    @Autowired
    private PaymentDao paymentDao;
    
    @Autowired
    private ParkingSpotDao parkingSpotDao;

    private static final Map<String, Object> ENTRY_LOCK = new ConcurrentHashMap<>();

    // 입차 시
    public OcrResponse processEntryImage(MultipartFile file) {
    	System.out.println("=> OcrService: processEntryImage | "+ new Date());
        return processImageCommon(file, "ENTRY");
    }

    // 출차 시
    public OcrResponse processExitImage(MultipartFile file) {
    	System.out.println("=> OcrService: processExitImage | "+ new Date());
        return processImageCommon(file, "EXIT");
    }

    // 번호판에 OCR 적용
    private OcrResponse processImageCommon(MultipartFile file, String type) {
        System.out.println("=> OcrService: processImageCommon | "+ new Date());
        List<String> debugImages = new ArrayList<>();
        File tempFile = null;

        try {
            // 이미지 읽기
        	BufferedImage original = ImageIO.read(file.getInputStream());
            debugImages.add(imageToBase64(original));

            // 전처리
            BufferedImage processedImage = preprocessBoldBlur(original);
            debugImages.add(imageToBase64(processedImage));

            // 테서랙트 OCR용 임시 파일 생성
            tempFile = File.createTempFile("ocr_target_", ".png");
            ImageIO.write(processedImage, "png", tempFile);

            // 테서랙트 OCR 설정
            ITesseract instance = new Tesseract();
            instance.setDatapath("tessdata");
            instance.setLanguage("kor+eng");
            instance.setPageSegMode(7);
            instance.setOcrEngineMode(1);
            
            // 인식률 높이기 위한 허용 숫자+한글 목록
            instance.setTessVariable("tessedit_char_whitelist",
            		"0123456789"
            		+ "가나다라마바사아자차카타파하거너더러머버서어저처커터퍼허고노도로모보소오조초코토포호구누두루무부수우주추쿠투푸후그느드르므브스으즈츠크트프흐육해공국합");
            
            // OCR 실행
            String rawResult = instance.doOCR(tempFile).replace("\n", "").trim();
            
            // OCR 실행해서 나온 번호판 결과
            String finalResult = parseLicensePlate(rawResult).replaceAll("\\s+", "");
            
            String entryTimeStr = "";
            String exitTimeStr = "";
            Integer parkingFee = -1;
            Boolean isMember = false;

            // OCR 성공 시 DB 로직 수행
            if (isValidResult(finalResult)) {
            	// 입차 시
            	if (type.equals("ENTRY")) {
                    Object lock = ENTRY_LOCK.computeIfAbsent(finalResult, k -> new Object());
                    synchronized (lock) {
                        ParkingLogDto existingLog = parkingLogDao.selectRecentEntryLog(finalResult);

                        if (existingLog != null && existingLog.getExitTime() == null) {
                            return new OcrResponse(finalResult, "이미 주차장에 입차된 차량입니다.", debugImages, 
                                    "ALREADY_PARKED", null, isMember, 0, existingLog.getParkingLogId(), null, null);
                       }

                        // DB에 입차 기록 생성
                        ParkingLogDto newLog = new ParkingLogDto();
                        newLog.setVehicleNum(finalResult);
                        boolean isMem = (memDao.checkMemberVehicle(finalResult) > 0);
                        newLog.setIsMember(isMem);
                        parkingLogDao.insertEntryLog(newLog);
                        
                        ParkingSpotDto spot = parkingSpotDao.findNearestAvailableSpot();
                        String allocatedSpotStr = "만차 (빈자리 없음)";

                        if (spot != null) {
                            parkingSpotDao.allocateSpot(spot.getSpotId(), newLog.getParkingLogId());
                            
                            char rowChar = (char) ('A' + spot.getParkingRow() - 1);
                            allocatedSpotStr = "지하 " + spot.getParkingFloor() + "층: " + rowChar + "-" + spot.getParkingColumn();
                        }

                        entryTimeStr = formatDateTime(LocalDateTime.now());
                        return new OcrResponse(finalResult, rawResult, debugImages, entryTimeStr, null, isMem, 0, newLog.getParkingLogId(), null, allocatedSpotStr);
                    }
                }
                
            	// 출차 시
                else if (type.equals("EXIT")) {
                    ParkingLogDto log = parkingLogDao.selectRecentEntryLog(finalResult);
                    if (log == null) {
                        return new OcrResponse(finalResult, "입차 기록이 없습니다.", debugImages, null, null, false, 0, null, null, null);
                    }

                    Long memId = null;
                    isMember = (memDao.checkMemberVehicle(finalResult) > 0);
                    if (isMember) {
                        memId = memDao.getMemIdByVehicle(finalResult);
                    }

                    boolean hasClinicVisit = (paymentDao.checkClinicVisit(finalResult) > 0);
                    LocalDateTime now = LocalDateTime.now();
                    long totalMin = Duration.between(log.getEntryTime(), now).toMinutes();
                    
                    // 요금 계산 (기본 30분 무료, 진료 시 +120분 추가 무료)
                    int feeToPay = calculateFee(totalMin, isMember, hasClinicVisit);

                    // 이미 정산기에서 결제를 완료했는지 확인 (결제 완료 시 요금 0원 처리)
                    if (log.getPaymentStatus() != null && log.getPaymentStatus()) {
                        feeToPay = 0; 
                    }

                    StringBuilder msgBuilder = new StringBuilder();
                    if (hasClinicVisit) msgBuilder.append("[진료할인 적용] ");
                    else if (isMember) msgBuilder.append("[회원할인 적용] ");

                    // 결제할 금액이 없거나 이미 결제한 경우 자동 출차 처리
                    if (feeToPay == 0) {
                        log.setExitTime(now);
                        log.setIsMember(isMember);
                        parkingLogDao.updateExitLog(log);
                        
                        parkingSpotDao.freeSpotByLogId(log.getParkingLogId());
                        
                        msgBuilder.append("무료 주차/정산 완료. 안녕히 가십시오.");
                        return new OcrResponse(finalResult, msgBuilder.toString(), debugImages, 
                                formatDateTime(log.getEntryTime()), formatDateTime(now), 
                                isMember, 0, log.getParkingLogId(), memId, null);
                    } 
                    // 결제가 필요한 경우
                    else {
                        msgBuilder.append("결제가 필요합니다.");
                        return new OcrResponse(finalResult, msgBuilder.toString(), debugImages,
                                formatDateTime(log.getEntryTime()), formatDateTime(now), 
                                isMember, feeToPay, log.getParkingLogId(), memId, null);
                    }
                }
            }
            return new OcrResponse(finalResult, rawResult, debugImages, entryTimeStr, exitTimeStr, isMember, parkingFee, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new OcrResponse("에러", "에러", debugImages, "에러", "에러", false, -1, null, null, null);
        } finally {
            if (tempFile != null) tempFile.delete();
        }
    }

    // 요금 계산 함수
    private int calculateFee(long minutes, boolean isMember, boolean hasClinicVisit) {
        int freeMinutes = 30;
        if (hasClinicVisit) freeMinutes += 120;

        if (minutes <= freeMinutes) return 0;

        long chargeMinutes = minutes - freeMinutes;
        int unit = (int) Math.ceil(chargeMinutes / 30.0);
        int rate = isMember ? 1000 : 2000;
        int totalAmount = unit * rate;

        long days = (minutes / (24 * 60)) + 1;
        int dailyLimit = isMember ? 15000 : 30000;
        
        return Math.min(totalAmount, (int)(days * dailyLimit));
    }

    // 추출한 문자열이 번호판 형태인지 확인
    private boolean isValidResult(String text) {
    	System.out.println("=> OcrService: isValidResult | "+ new Date());
        return text != null && !text.equals("인식 실패") && !text.contains("에러") && !text.trim().isEmpty();
    }

    // 시간 보여주는 모양 변경
    private String formatDateTime(LocalDateTime time) {
    	System.out.println("=> OcrService: formatDateTime | "+ new Date());
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    // 전처리 종합
    private BufferedImage preprocessBoldBlur(BufferedImage source) {
    	System.out.println("=> OcrService: preprocessBoldBlur | "+ new Date());
        BufferedImage resized = resizeImage(source, 2);		// 확대 2x
        BufferedImage bold = applyDilation(resized);		// 글자 굵게
        BufferedImage smoothBold = applyGaussianBlur(bold);	// 가우시안 블러
        return addPadding(smoothBold, 50);					// 패딩해서 리턴
    }

    // 확대 2x
    private BufferedImage resizeImage(BufferedImage original, int scale) {
    	System.out.println("=> OcrService: resizeImage | "+ new Date());
        int w = original.getWidth() * scale;
        int h = original.getHeight() * scale;
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, w, h, null);
        g.dispose();
        return resized;
    }
    
    // 글자 굵게
    private BufferedImage applyDilation(BufferedImage source) {
    	System.out.println("=> OcrService: applyDilation | "+ new Date());
        int w = source.getWidth();
        int h = source.getHeight();
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int minVal = 255;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int val = source.getRGB(x + dx, y + dy) & 0xFF;
                        if (val < minVal) minVal = val;
                    }
                }
                int newPixel = (255 << 24) | (minVal << 16) | (minVal << 8) | minVal;
                dest.setRGB(x, y, newPixel);
            }
        }
        return dest;
    }

    // 가우시안 블러
    private BufferedImage applyGaussianBlur(BufferedImage source) {
    	System.out.println("=> OcrService: applyGaussianBlur | "+ new Date());
        float[] matrix = {
            1/16f, 1/8f, 1/16f,
            1/8f,  1/4f, 1/8f,
            1/16f, 1/8f, 1/16f,
        };
        Kernel kernel = new Kernel(3, 3, matrix);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(source, null);
    }

    // 패딩
    private BufferedImage addPadding(BufferedImage original, int padding) {
    	System.out.println("=> OcrService: addPadding | "+ new Date());
        BufferedImage padded = new BufferedImage(original.getWidth() + padding * 2, original.getHeight() + padding * 2, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = padded.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, padded.getWidth(), padded.getHeight());
        g.drawImage(original, padding, padding, null);
        g.dispose();
        return padded;
    }

    // 번호판 문자열 추출
    private String parseLicensePlate(String text) {
    	System.out.println("=> OcrService: parseLicensePlate | "+ new Date());
        String cleanText = text.replaceAll("[^0-9가-힣]", "");
        Pattern fullPattern = Pattern.compile("([0-9]{2,3})([가-힣])([0-9]{4})$");
        Matcher fullMatcher = fullPattern.matcher(cleanText);
        
        if (fullMatcher.find())
            return fullMatcher.group(0);
        
        Pattern lastFourPattern = Pattern.compile("([0-9]{4})$");
        Matcher lastFourMatcher = lastFourPattern.matcher(cleanText);
        
        if (lastFourMatcher.find()) 
            return "뒷번호: " + lastFourMatcher.group(1);
        return "인식 실패";
    }

    // BufferedImage -> Base64 String 변환
    private String imageToBase64(BufferedImage image) throws IOException {
    	System.out.println("=> OcrService: imageToBase64 | "+ new Date());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }
}*/