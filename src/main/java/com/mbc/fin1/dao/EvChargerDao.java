package com.mbc.fin1.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mbc.fin1.dto.EvChargerDto;

@Mapper
public interface EvChargerDao {

    /**
     * 전체 충전기 목록 조회
     */
    List<EvChargerDto> selectEvChargerList(@Param("floor") String floor);

    /**
     * 충전기 ID 기준 단건 조회
     */
    EvChargerDto selectEvChargerById(@Param("evChargerId") String evChargerId);

    /**
     * 실시간 현황 그래프/요약 조회
     */
    List<Map<String, Object>> selectChargingSummaryByHour(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 실시간 이벤트 로그 조회
     */
    List<Map<String, Object>> selectChargingLogs(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("status") String status
    );
}