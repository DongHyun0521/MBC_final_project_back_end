// mbcFinalProject1 - com.mbc.fin1.dao - HolidayDao.java
package com.mbc.fin1.dao;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HolidayDao {
    List<LocalDate> findAllHolidayDates();	// 공휴일 날짜만 리스트로 불러오기
}