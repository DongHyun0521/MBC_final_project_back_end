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
    private LocalDateTime entryTime;	// 입차 시간
    private LocalDateTime exitTime;		// 출차 시간
    private Boolean isMember;			// 회원 여부
    private Boolean paymentStatus;		// 결제 여부
}