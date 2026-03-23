// mbcFinalProject1 - com.mbc.fin1.dto - ReservationDto.java
package com.mbc.fin1.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long reservationId;	// PK
    private Long memId;			// FK
    private Long medDeptId;		// FK
    private Long doctorId;		// FK
    
    private LocalDate reservationDate;	// 예약 날짜 (yyyy-MM-dd)
    private LocalTime reservationTime;	// 예약 시간 (HH:mm:ss)
    
    private String reservationType;				// 예약 종류
    private String visitType;					// 초진/재진
    private String reservationStatus;			// 상태
    private String reservationMemo;				// 메모
    private LocalDateTime reservationMadeTime;	// 예약 당시 시간
}