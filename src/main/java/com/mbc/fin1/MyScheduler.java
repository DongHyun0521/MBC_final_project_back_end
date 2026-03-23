// mbcFinalProject1 - com.mbc.fin1 - MyScheduler.java
package com.mbc.fin1;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.mbc.fin1.dao.AdminDao;
import com.mbc.fin1.service.MedService;
import com.mbc.fin1.service.ParkingPredictionService;

import jakarta.annotation.PostConstruct;

@Component
public class MyScheduler {
	
    @Autowired
    private AdminDao adminDao;
    
    @Autowired
    private MedService medService;
    
    @Autowired
    private ParkingPredictionService predictionService;
    
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
    
    // 🌟 서버가 켜지자마자 알람 무시하고 일단 1번 강제 실행!
    @PostConstruct
    public void runOnStartup() {
        System.out.println("🚀 [서버 기동] 스케줄러 대기 전에 최초 1회 예측을 즉시 실행합니다!");
        predictionService.predictHalfHourlyParking();
    }

    // ⏰ [수정] 매시간 정각(00분)과 30분마다 알아서 실행
    @Scheduled(cron = "0 0,30 * * * *")
    public void runParkingPrediction() {
        System.out.println("⏰ [스케줄러 작동] 30분 단위 주차 예측 AI를 호출합니다...");
        predictionService.predictHalfHourlyParking(); // 💡 30분 단위 메서드로 변경
    }
}