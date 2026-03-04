// mbcFinalProject1 - com.mbc.mid.dto - MemberVehicleDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVehicleDto {
    private Long vehicleId;     // PK
    private Long memId;         // FK
    private String vehicleNum;  // 차량 번호
    private String vehicleType; // 차종
    private String fuelType;    // 연료
    private String createTime;  // 등록 일시
}