package com.mbc.fin1.dto;

import lombok.Data;

@Data
public class EvPredictionDto {

    private Integer pred_class;           // 모델 판단 클래스 (0: 정상, 1: 점검, 2: 위험)
    private String status;                // 화면용 상태 텍스트 (정상 / 점검 / 위험)
    private String ai_status;             // DB/로직용 상태 코드 (NORMAL / CHECK / RISK)
    private String action;                // 수행 액션
    private Boolean alarm;                // 알람 여부
    private String message;               // 사용자 메시지
    private String main_reason;           // 주요 원인
    private String device_status;         // 현재 기기 상태
    private Boolean inspection_requested; // 점검 요청 여부
    private Boolean shutdown_done;        // 전원 차단 여부

    private Double fault_prob_7d;         // 7일 내 고장 확률

    private Double prob_normal;           // 정상 확률
    private Double prob_check;            // 점검 확률
    private Double prob_risk;             // 위험 확률

    private Double temperature;           // 현재 온도
    private Double voltage;               // 현재 전압
    private Double current;               // 현재 전류
}