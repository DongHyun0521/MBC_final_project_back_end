// mbcFinalProject1 - com.mbc.fin1.dto - ParkingLogDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLogDto {
	private Long parkingLogId;		// PK
	private Integer reservationId;	// FK
	
	private String vehicleImg;		// 원본 이미지 경로
    private String licensePlateImg;	// 번호판 이미지 경로
	
    private String licensePlateCountry;	// 국가코드 (BRA, CHN, EUR, IND, KOR)
    private Double countryAccuracy;		// 국가 판별 정확도
    private String vehicleNum;			// 차량 번호 (OCR 결과 / 수정 결과)
    private Double ocrAccuracy;         // PaddleOCR 정확도
    private Boolean isEvLicensePlate;	// 전기차(하늘색 번호판) 여부
    
    private LocalDateTime entryTime;	// 입차 시간
    private LocalDateTime exitTime;		// 출차 시간
    
    private Boolean isMember;		// 회원 여부
    private Integer parkingFee;		// 주차 요금
    private Boolean isPaid;			// 결제 여부
    private Integer discountAmount;	// 할인 금액
    private String payType;			// 결제 방식
    
    private Integer adminStaffId;		// 수정 관리자 FK
    private Boolean isModified;			// 수정 여부
    private LocalDateTime modifyTime;	// 수정 시간
    private Integer del;				// 삭제 여부
    
    private String recommendedSpot;	// 추천 주차 위치
    private String parkingStatus;	// 주차 상태
    private String empNumber;		// 관리자 사원번호
    private Long duration;			// 체류 시간(분)
}