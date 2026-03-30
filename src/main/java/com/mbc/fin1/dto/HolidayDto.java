// mbcFinalProject1 - com.mbc.fin1.dto - HolidayDto.java
package com.mbc.fin1.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDto {
    private LocalDate holidayDate;	// PK
    private String holidayName;		// 공휴일 이름
}