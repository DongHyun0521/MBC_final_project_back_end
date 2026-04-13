// mbcFinalProject1 - com.mbc.fin1.controller - ParkingLogController.java
package com.mbc.fin1.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.service.ParkingLogService;

@RestController
@RequestMapping("/parking-log")
public class ParkingLogController {

    @Autowired private ParkingLogService parkingLogService;

    // 차량 입차 시
    @PostMapping("/entry")
    public ResponseEntity<?> vehicleEntry(@RequestParam("file") MultipartFile file) {
        try {
            ParkingLogDto result = parkingLogService.processEntry(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("입차 처리 중 오류 발생: " + e.getMessage());
        }
    }

    // 주차 로그 출력
    @GetMapping("/logs")
    public ResponseEntity<List<ParkingLogDto>> getLogs() {
        return ResponseEntity.ok(parkingLogService.selectAllParkingLogs());
    }

    // 주차 로그 수정
    @PutMapping("/update")
    public ResponseEntity<?> updateLog(@RequestBody Map<String, Object> params) {
        try {
        	Long logId = Long.valueOf(params.get("parkingLogId").toString());
            String newNum = (String) params.get("vehicleNum");
            String newCountry = (String) params.get("licensePlateCountry");
            Integer adminId = Integer.valueOf(params.get("adminStaffId").toString());
            
            parkingLogService.updateLogByAdmin(logId, newNum, newCountry, adminId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("수정 실패");
        }
    }
    
    // 주차 로그 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLog(@PathVariable("id") Long id, @RequestParam(defaultValue = "1") Integer adminStaffId) {
        try {
            parkingLogService.deleteParkingLog(id, adminStaffId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패");
        }
    }
}