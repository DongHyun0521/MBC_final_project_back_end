// mbcFinalProject1 - com.mbc.fin1.controller - ParkingSpotController.java
package com.mbc.fin1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.fin1.dao.ParkingSpotDao;
import com.mbc.fin1.dto.ParkingSpotDto;

@RestController
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    @Autowired private ParkingSpotDao parkingSpotDao;

    // 관리자 주차장 현황 맵: 모든 자리 정보 (차량 + 회원여부 + 충전기 자리 여부)
    @GetMapping("/all")
    public ResponseEntity<List<ParkingSpotDto>> getAllSpots() {
        return ResponseEntity.ok(parkingSpotDao.findAllSpots());
    }
}
