// mbcFinalProject1 - com.mbc.mid.dto - MedDeptDto.java
package com.mbc.fin1.dto;
public class MedDeptDto {
    private Long medDeptId;         // PK
    private String deptName;        // 부서명
    private String deptLocation;    // 위치
    private String deptPhoneNumber; // 전화번호
    
	public MedDeptDto() {
		super();
	}
	public MedDeptDto(Long medDeptId, String deptName, String deptLocation, String deptPhoneNumber) {
		super();
		this.medDeptId = medDeptId;
		this.deptName = deptName;
		this.deptLocation = deptLocation;
		this.deptPhoneNumber = deptPhoneNumber;
	}
	public Long getMedDeptId() {
		return medDeptId;
	}
	public void setMedDeptId(Long medDeptId) {
		this.medDeptId = medDeptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getDeptLocation() {
		return deptLocation;
	}
	public void setDeptLocation(String deptLocation) {
		this.deptLocation = deptLocation;
	}
	public String getDeptPhoneNumber() {
		return deptPhoneNumber;
	}
	public void setDeptPhoneNumber(String deptPhoneNumber) {
		this.deptPhoneNumber = deptPhoneNumber;
	}
    
}