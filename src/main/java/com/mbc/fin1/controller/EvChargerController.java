package com.mbc.fin1.controller;

import java.util.List;

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
     * 충전기 ID 기준 상세 조회
     */
    @GetMapping("/{evChargerId}")
    public EvChargerDto getEvChargerById(@PathVariable String evChargerId) {
        return evChargerService.getEvChargerById(evChargerId);
    }
}