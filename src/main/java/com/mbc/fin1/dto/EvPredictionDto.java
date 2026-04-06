package com.mbc.fin1.dto;

import lombok.Data;

@Data
public class EvPredictionDto {
    private Integer pred_class;						// 모델 판단 위험 클래스(0: 정상, 1:점검, 2:위험)
    private String status;							// 상태 텍스트 (정상/ 점검/ 위험)
    private String action;							// 수행 액션(현장 출동, 작동 중지)
    private Boolean alarm;							// 알람 여부(true시 위험 알람)
    private String message;							// 사용자에게 보여줄 메세지
    private String main_reason;						// 고장 주요 원인
    private String device_status;					// 현재 기기 상태(작동중 / 중지)
    private Boolean inspection_requested;			// 점검 요청 여부
    private Double prob_normal;						// 정상 확률
    private Double prob_warning;					// 점검 확률
    private Double prob_risk;						// 위험 확률
    private Double fault_prob_7d;  					// 7일내 고장 확률
}
