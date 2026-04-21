package com.mbc.fin1.dao;

import java.util.List;

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
}