package com.mbc.fin1.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EvChargerDto {

    /** 충전기 ID (PK) - 예: 1F-D-08 */
    private String evChargerId;

    /** parking_spot 테이블과 연결되는 주차면 PK */
    private Integer parkingSpotId;

    /** 충전기 타입 (FAST / SLOW) */
    private String chargerType;

    /** 충전기 원본 상태값 */
    private String chargerStatus;

    /** 충전기 생성 시각 */
    private LocalDateTime createTime;

    /** 층 탭 조회용 원본 층값 - 예: 1F, 2F */
    private String floor;

    /** 화면 표시용 층 라벨 - 예: B1, B2 */
    private String floorLabel;

    /** 화면 표시용 상태 라벨 - 예: 대기, 사용 중, 점검, 위험, 고장, 전원 차단 */
    private String statusLabel;
}