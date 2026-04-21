package com.mbc.fin1.service;

import java.util.List;

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
}