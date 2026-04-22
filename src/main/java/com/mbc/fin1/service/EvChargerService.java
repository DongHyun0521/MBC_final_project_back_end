package com.mbc.fin1.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mbc.fin1.dao.EvChargerDao;
import com.mbc.fin1.dto.EvChargerDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvChargerService {

    private final EvChargerDao evChargerDao;

    /**
     * 전체 충전기 목록 조회
     */
    public List<EvChargerDto> getEvChargerList(String floor) {
        return evChargerDao.selectEvChargerList(floor);
    }

    /**
     * 충전기 ID 기준 단건 조회
     */
    public EvChargerDto getEvChargerById(String evChargerId) {
        return evChargerDao.selectEvChargerById(evChargerId);
    }

    /**
     * 실시간 현황 그래프/요약 조회
     */
    public List<Map<String, Object>> getChargingSummaryByHour(String startDate, String endDate) {
        return evChargerDao.selectChargingSummaryByHour(startDate, endDate);
    }

    /**
     * 실시간 이벤트 로그 조회
     */
    public List<Map<String, Object>> getChargingLogs(String startDate, String endDate, String status) {
        return evChargerDao.selectChargingLogs(startDate, endDate, status);
    }
}