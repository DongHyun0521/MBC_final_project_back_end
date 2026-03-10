// mbcFinalProject1 - com.mbc.fin1.dto - PaymentDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long payId;             // PK
    private Long parkingLogId;      // FK
    private Long memId;             // FK
    private Integer amount;         // 결제 금액
    private String payMethod;       // 결제 수단
    private LocalDateTime payDate;	// 결제 일시
}