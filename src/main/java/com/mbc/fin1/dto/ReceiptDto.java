// mbcFinalProject1 - com.mbc.fin1.dto - ReceiptDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDto {
	private Long receiptId;         // PK (payId에서 변경됨)
    private Long parkingLogId;      // FK
    private Long memId;             // FK
    private String payMethod;       // 결제 수단
    private Integer amount;         // 총 결제 금액
    private Integer parkingFee;     // 주차 요금
    private Integer evChargingFee;  // 충전 요금
    private LocalDateTime payDate;  // 결제 일시
}