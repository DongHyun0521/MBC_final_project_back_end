// mbcFinalProject1 - com.mbc.fin1.dto - ReservationDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long reservationId;     		// PK
    private Long memId;             		// FK
    private Long medDeptId;         		// FK
    private Long doctorId;          		// FK
    private Integer reservationDate; 		// 예약 날짜
    private LocalDateTime reservationTime;	// 예약 시간
    private String reservationType;  		// 예약 종류
    private String visitType;        		// 초진/재진
    private String reservationStatus;		// 상태
    private String reservationMemo;  		// 메모
    private String reservationMadeTime;		// 예약 당시 시간
}