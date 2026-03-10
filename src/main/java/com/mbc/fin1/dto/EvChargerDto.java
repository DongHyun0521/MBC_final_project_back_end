// mbcFinalProject1 - com.mbc.fin1.dto - EvChargerDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvChargerDto {
    private Integer evChargerId;	// PK
    private Integer parkingSpotId;	// FK
    private String evChargerType;	// 전기차 충전기 종류 (급속, 중속, 완속)
    private String evChargerState;	// 전기차 충전기 상태 (STANDBY, CHARGING, ERROR)
    private Boolean evChargerPower;	// 전기차 충전기 전원
}
