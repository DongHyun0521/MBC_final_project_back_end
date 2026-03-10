// mbcFinalProject1 - com.mbc.fin1 - MyScheduler.java
package com.mbc.fin1;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.mbc.fin1.dao.AdminDao;
import com.mbc.fin1.service.MedService;

@Component
public class MyScheduler {
	
    @Autowired
    private AdminDao adminDao;
    
    @Autowired
    private MedService medService;
    
    /*
    초 : 0~59     분 : 0~59     시 : 0~23
    일 : 1~31     월 : 1~12     요일 : 0~7 (MON~SUN)
    * : 매번
                     초 분 시 일 월 요일              */
    // 고객의소리 30일 후 자동 삭제 (delete)
    @Scheduled(cron = "0 0 0 * * *") 
    public void autoDeleteVoc() {
        System.out.println("admin이 삭제한 voc 영구삭제 중...");
        try {
            int count = adminDao.permanentDeleteVoc();
            if(count > 0) System.out.println("=> voc " + count + "개 영구 삭제 완료" + new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 자동으로 예약 미방문으로 바꾸기 (예약->미방문)
    @Scheduled(fixedRate = 60000)			// 1분마다 실행 (단위: 밀리초)
    //@Scheduled(cron = "0 1,31 * * * *")	// 매시간 1분, 31분에 실행
    public void checkNoShow() {
        try {
            int updatedCount = medService.processNoShowReservations();
            if (updatedCount > 0) {
                System.out.println("=> Scheduler: " + updatedCount + "건 예약 미방문 처리 완료" + new Date());
            }
        } catch (Exception e) {
            System.err.println("=> Scheduler Error: " + e.getMessage());
        }
    }
}