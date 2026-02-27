// mbcFinalProject1 - com.mbc.mid.dto - ParkingLogDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;

public class ParkingLogDto {
	private Long parkingLogId;			// PK
    private String vehicleNum;			// 차량 번호 (OCR 결과)
    private LocalDateTime entryTime;	// 입차 시간
    private LocalDateTime exitTime;		// 출차 시간
    private Boolean isMember;			// 회원 여부
    private Boolean paymentStatus;		// 결제 여부
    
	public ParkingLogDto() {}
	public ParkingLogDto(Long parkingLogId, String vehicleNum, LocalDateTime entryTime, LocalDateTime exitTime, Boolean isMember, Boolean paymentStatus) {
		this.parkingLogId = parkingLogId;
		this.vehicleNum = vehicleNum;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.isMember = isMember;
		this.paymentStatus = paymentStatus;
	}
	public Long getParkingLogId() {
		return parkingLogId;
	}
	public void setParkingLogId(Long parkingLogId) {
		this.parkingLogId = parkingLogId;
	}
	public String getVehicleNum() {
		return vehicleNum;
	}
	public void setVehicleNum(String vehicleNum) {
		this.vehicleNum = vehicleNum;
	}
	public LocalDateTime getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}
	public LocalDateTime getExitTime() {
		return exitTime;
	}
	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}
	public Boolean getIsMember() {
		return isMember;
	}
	public void setIsMember(Boolean isMember) {
		this.isMember = isMember;
	}
	public Boolean getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	@Override
	public String toString() {
		return "ParkingLogDto [parkingLogId=" + parkingLogId + ", vehicleNum=" + vehicleNum + ", entryTime=" + entryTime
				+ ", exitTime=" + exitTime + ", isMember=" + isMember + ", paymentStatus=" + paymentStatus + "]";
	}
}
