// mbcFinalProject1 - com.mbc.fin1.service - HolidayService.java
package com.mbc.fin1.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbc.fin1.dao.HolidayDao; // 💡 새롭게 만들 DAO

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HolidayService {

    @Autowired
    private HolidayDao holidayDao;
    
    private Set<LocalDate> holidayCache = new HashSet<>();

    // DB에서 공휴일 리스트 미리 불러오기
    @PostConstruct
    public void initHolidays() {
    	System.out.println("=> HolidayService: initHolidays | "+ new Date());
        // DB에서 공휴일 날짜만 리스트로 미리 불러오기
        List<LocalDate> holidayDates = holidayDao.findAllHolidayDates();
        
        if (holidayDates != null) holidayCache.addAll(holidayDates);
        
        System.out.println("=> 공휴일 " + holidayCache.size() + "개 불러오기 완료 | "+ new Date());
    }

    // 공휴일인지 확인
    public boolean isHoliday(LocalDate date) {
    	System.out.println("=> HolidayService: isHoliday | "+ new Date());
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        // 토/일요일이면 무조건 휴일
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return true;
        }
        // 평일인데 공휴일 리스트에 있으면 휴일
        return holidayCache.contains(date);
    }
}