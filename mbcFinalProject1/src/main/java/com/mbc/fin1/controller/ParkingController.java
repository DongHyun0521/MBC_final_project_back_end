// mbcFinalProject1 - com.mbc.fin1.controller - ParkingController.java
package com.mbc.fin1.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.fin1.dao.ParkingSpotDao;
import com.mbc.fin1.dto.ParkingSpotDto;

@RestController
@RequestMapping("/api/parking")
@CrossOrigin(origins = "http://localhost:5173")
public class ParkingController {

    @Autowired
    private ParkingSpotDao parkingSpotDao;

    // 주차 자리 전체 출력하기
    @GetMapping("/spots")
    public List<ParkingSpotDto> getAllSpots() {
        return parkingSpotDao.findAllSpots();
    }
}