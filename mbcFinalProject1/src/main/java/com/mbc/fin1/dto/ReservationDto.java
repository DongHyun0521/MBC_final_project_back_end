// mbcFinalProject1 - com.mbc.mid.dto - ReservationDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;

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
    
	public ReservationDto() {
		super();
	}
	public ReservationDto(Long reservationId, Long memId, Long medDeptId, Long doctorId, Integer reservationDate,
			LocalDateTime reservationTime, String reservationType, String visitType, String reservationStatus,
			String reservationMemo, String reservationMadeTime) {
		super();
		this.reservationId = reservationId;
		this.memId = memId;
		this.medDeptId = medDeptId;
		this.doctorId = doctorId;
		this.reservationDate = reservationDate;
		this.reservationTime = reservationTime;
		this.reservationType = reservationType;
		this.visitType = visitType;
		this.reservationStatus = reservationStatus;
		this.reservationMemo = reservationMemo;
		this.reservationMadeTime = reservationMadeTime;
	}
	public Long getReservationId() {
		return reservationId;
	}
	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public Long getMedDeptId() {
		return medDeptId;
	}
	public void setMedDeptId(Long medDeptId) {
		this.medDeptId = medDeptId;
	}
	public Long getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}
	public Integer getReservationDate() {
		return reservationDate;
	}
	public void setReservationDate(Integer reservationDate) {
		this.reservationDate = reservationDate;
	}
	public LocalDateTime getReservationTime() {
		return reservationTime;
	}
	public void setReservationTime(LocalDateTime reservationTime) {
		this.reservationTime = reservationTime;
	}
	public String getReservationType() {
		return reservationType;
	}
	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
	}
	public String getVisitType() {
		return visitType;
	}
	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}
	public String getReservationStatus() {
		return reservationStatus;
	}
	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}
	public String getReservationMemo() {
		return reservationMemo;
	}
	public void setReservationMemo(String reservationMemo) {
		this.reservationMemo = reservationMemo;
	}
	public String getReservationMadeTime() {
		return reservationMadeTime;
	}
	public void setReservationMadeTime(String reservationMadeTime) {
		this.reservationMadeTime = reservationMadeTime;
	}
    
}