// mbcFinalProject1 - com.mbc.fin1.dto - EvChargingLogDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvChargingLogDto {
    private Long evChargingLogId;				// PK
    private Integer evChargerId;				// FK
    private Long parkingLogId;					// FK
    private LocalDateTime evChargingStartTime;	// 충전 시작 시간
    private LocalDateTime evChargingEndTime;	// 충전 종료 시간
    private Integer evChargingFee;				// 충전 요금
    private String evChargingEndReason;			// 종료 사유 (NORMAL, ERROR)
}