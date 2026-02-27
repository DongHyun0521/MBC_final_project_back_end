// mbcFinalProject1 - com.mbc.mid.dto - PaymentDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;

public class PaymentDto {
    private Long payId;             // PK
    private Long parkingLogId;      // FK
    private Long memId;             // FK
    private Integer amount;         // 결제 금액
    private String payMethod;       // 결제 수단
    private LocalDateTime payDate;	// 결제 일시
    
	public PaymentDto() {
		super();
	}
	public PaymentDto(Long payId, Long parkingLogId, Long memId, Integer amount, String payMethod,
			LocalDateTime payDate) {
		super();
		this.payId = payId;
		this.parkingLogId = parkingLogId;
		this.memId = memId;
		this.amount = amount;
		this.payMethod = payMethod;
		this.payDate = payDate;
	}
	public Long getPayId() {
		return payId;
	}
	public void setPayId(Long payId) {
		this.payId = payId;
	}
	public Long getParkingLogId() {
		return parkingLogId;
	}
	public void setParkingLogId(Long parkingLogId) {
		this.parkingLogId = parkingLogId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public LocalDateTime getPayDate() {
		return payDate;
	}
	public void setPayDate(LocalDateTime payDate) {
		this.payDate = payDate;
	}
    
}