// mbcFinalProject1 - com.mbc.fin1.dto - ParkingSpotDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotDto {
    private Integer spotId;			// PK
    private Long parkingLogId;		// FK
    private Integer parkingFloor;	// 층
    private Integer parkingRow;		// 행(가로)
    private Integer parkingColumn;	// 열(세로)
    private Boolean isParked;		// 주차 여부
    
    private String vehicleNum;	// 차량 번호
    private String entryTime;	// 입차 시간
}