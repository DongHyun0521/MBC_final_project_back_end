package com.mbc.fin1.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.fin1.dto.EvChargerDto;
import com.mbc.fin1.service.EvChargerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ev/chargers")
public class EvChargerController {

    private final EvChargerService evChargerService;

    /**
     * 전체 충전기 목록 조회
     */
    @GetMapping
    public List<EvChargerDto> getEvChargerList(
            @RequestParam(required = false) String floor
    ) {
        return evChargerService.getEvChargerList(floor);
    }

    /**
     * 충전기 ID 기준 단건 조회
     */
    @GetMapping("/{evChargerId}")
    public EvChargerDto getEvChargerById(@PathVariable String evChargerId) {
        return evChargerService.getEvChargerById(evChargerId);
    }

    /**
     * 실시간 현황 그래프/요약 조회
     */
    @GetMapping("/summary")
    public List<Map<String, Object>> getChargingSummaryByHour(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        return evChargerService.getChargingSummaryByHour(startDate, endDate);
    }

    /**
     * 실시간 이벤트 로그 조회
     */
    @GetMapping("/logs")
    public List<Map<String, Object>> getChargingLogs(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String status
    ) {
        return evChargerService.getChargingLogs(startDate, endDate, status);
    }
}