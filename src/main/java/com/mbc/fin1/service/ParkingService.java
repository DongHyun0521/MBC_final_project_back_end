// mbcFinalProject1 - com.mbc.fin1.service - ParkingService.java
package com.mbc.fin1.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dao.ParkingLogDao;
import com.mbc.fin1.dao.ParkingSpotDao;
import com.mbc.fin1.dao.ReceiptDao;
import com.mbc.fin1.dto.OcrDto;
import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.dto.ParkingSpotDto;

@Service
@Transactional
public class ParkingService {

    @Autowired private ParkingLogDao parkingLogDao;
    @Autowired private ParkingSpotDao parkingSpotDao;
    @Autowired private MemDao memDao;
    @Autowired private ReceiptDao receiptDao;

    // 💡 동시 입차 방지 락
    private static final Map<String, Object> ENTRY_LOCK = new ConcurrentHashMap<>();

    // ============================== [ 1. 입차 처리 ] ==============================
    public OcrDto processEntry(Map<String, Object> ocrData) {
        String vehicleNum = (String) ocrData.get("plateNumber");
        String country = (String) ocrData.get("country");
        Boolean isEv = (Boolean) ocrData.get("isEv");
        List<String> debugImages = (List<String>) ocrData.get("debugImages");

        if (!isValidResult(vehicleNum)) {
            return new OcrDto("에러", "에러", debugImages, country, isEv, "에러", null, false, -1, null, null, null);
        }

        Object lock = ENTRY_LOCK.computeIfAbsent(vehicleNum, k -> new Object());
        synchronized (lock) {
            ParkingLogDto existingLog = parkingLogDao.selectRecentEntryLog(vehicleNum);

            // 이미 주차된 차량 방어 로직
            if (existingLog != null && existingLog.getExitTime() == null) {
                return new OcrDto(vehicleNum, "이미 주차장에 입차된 차량입니다.", debugImages, 
                        country, isEv, "ALREADY_PARKED", null, false, 0, existingLog.getParkingLogId(), null, null);
            }

            // 새 입차 기록 생성
            ParkingLogDto newLog = new ParkingLogDto();
            newLog.setVehicleNum(vehicleNum);
            newLog.setLicensePlateCountry(country);
            newLog.setIsEvLicensePlate(isEv);
            
            boolean isMember = (memDao.checkMemberVehicle(vehicleNum) > 0);
            newLog.setIsMember(isMember);
            parkingLogDao.insertEntryLog(newLog); // DB 저장

            // 자리 배정 로직
            ParkingSpotDto spot = (isEv != null && isEv) ? parkingSpotDao.findNearestAvailableEvSpot() : null;
            if (spot == null) spot = parkingSpotDao.findNearestAvailableSpot();

            String allocatedSpotStr = "만차 (빈자리 없음)";
            if (spot != null) {
                parkingSpotDao.allocateSpot(spot.getSpotId(), newLog.getParkingLogId());
                char rowChar = (char) ('A' + spot.getParkingRow() - 1);
                allocatedSpotStr = "지하 " + spot.getParkingFloor() + "층: " + rowChar + "-" + spot.getParkingColumn();
                if (isEv != null && isEv && spot.getParkingFloor() == 1) {
                    allocatedSpotStr += " ⚡ (전기차 전용 구역)";
                }
            }

            String entryTimeStr = formatDateTime(LocalDateTime.now());
            return new OcrDto(vehicleNum, vehicleNum, debugImages, country, isEv, entryTimeStr, null, isMember, 0, newLog.getParkingLogId(), null, allocatedSpotStr);
        }
    }

    // ============================== [ 2. 출차 처리 (요금 계산) ] ==============================
    public OcrDto processExit(Map<String, Object> ocrData) {
        String vehicleNum = (String) ocrData.get("plateNumber");
        String country = (String) ocrData.get("country");
        Boolean isEv = (Boolean) ocrData.get("isEv");
        List<String> debugImages = (List<String>) ocrData.get("debugImages");

        ParkingLogDto log = parkingLogDao.selectRecentEntryLog(vehicleNum);
        if (log == null) {
            return new OcrDto(vehicleNum, "입차 기록이 없습니다.", debugImages, country, isEv, null, null, false, 0, null, null, null);
        }

        boolean isMember = (memDao.checkMemberVehicle(vehicleNum) > 0);
        Long memId = isMember ? memDao.getMemIdByVehicle(vehicleNum) : null;
        boolean hasClinicVisit = (receiptDao.checkClinicVisit(vehicleNum) > 0);
        
        LocalDateTime now = LocalDateTime.now();
        long totalMin = Duration.between(log.getEntryTime(), now).toMinutes();
        int feeToPay = calculateFee(totalMin, isMember, hasClinicVisit);

        StringBuilder msgBuilder = new StringBuilder();
        if (hasClinicVisit) msgBuilder.append("[진료할인 적용] ");
        else if (isMember) msgBuilder.append("[회원할인 적용] ");

        // 요금이 0원이면 즉시 출차 완료 처리!
        if (feeToPay == 0) {
            completePaidExit(log.getParkingLogId(), 0); // 💡 핵심 공통 메서드 호출
            msgBuilder.append("무료 주차/정산 완료. 안녕히 가십시오.");
            return new OcrDto(vehicleNum, msgBuilder.toString(), debugImages, country, isEv, formatDateTime(log.getEntryTime()), formatDateTime(now), isMember, 0, log.getParkingLogId(), memId, null);
        } 
        // 요금이 남았으면 결제 대기
        else {
            msgBuilder.append("결제가 필요합니다.");
            return new OcrDto(vehicleNum, msgBuilder.toString(), debugImages, country, isEv, formatDateTime(log.getEntryTime()), formatDateTime(now), isMember, feeToPay, log.getParkingLogId(), memId, null);
        }
    }

    // ============================== [ 3. 영수증 서비스가 호출할 최종 출차 승인 ] ==============================
    // 결제가 완료되었거나, 요금이 0원일 때 차단기를 열어주고 DB를 갱신하는 공통 메서드
    public void completePaidExit(Long parkingLogId, Integer finalFee) {
        ParkingLogDto log = parkingLogDao.findById(parkingLogId.intValue());
        if (log != null) {
            log.setExitTime(LocalDateTime.now());
            log.setIsMember(memDao.checkMemberVehicle(log.getVehicleNum()) > 0);
            log.setParkingFee(finalFee);
            
            parkingLogDao.updateExitLog(log);                 // 로그 업데이트
            parkingSpotDao.freeSpotByLogId(parkingLogId);     // 주차 자리 비움
        }
    }

    // --- 유틸 메서드 ---
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
}