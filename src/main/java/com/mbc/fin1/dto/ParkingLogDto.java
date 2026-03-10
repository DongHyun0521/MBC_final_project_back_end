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
	private Long parkingLogId;			// PK
    private String vehicleNum;			// 차량 번호 (OCR 결과)
    private String licensePlateCountry;	// 국가코드 (한국KOR, 말레이시아MYS, 중국CHN, 브라질BRA 등)
    private Boolean isEvLicensePlate;	// 전기차(하늘색 번호판) 여부
    private LocalDateTime entryTime;	// 입차 시간
    private LocalDateTime exitTime;		// 출차 시간
    private Boolean isMember;			// 회원 여부
    private Integer parkingFee;			// 주차 요금
}