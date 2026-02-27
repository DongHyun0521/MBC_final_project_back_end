// mbcFinalProject1 - com.mbc.mid.dto - AdminDeptDto.java
package com.mbc.fin1.dto;

public class AdminDeptDto {
	private Long adminDeptId;		// PK
    private String deptName;		// 부서명
    private String deptLocation;	// 위치
    private String deptPhoneNumber;	// 전화번호
    
	public AdminDeptDto() {
		super();
	}
	public AdminDeptDto(Long adminDeptId, String deptName, String deptLocation, String deptPhoneNumber) {
		super();
		this.adminDeptId = adminDeptId;
		this.deptName = deptName;
		this.deptLocation = deptLocation;
		this.deptPhoneNumber = deptPhoneNumber;
	}
	public Long getAdminDeptId() {
		return adminDeptId;
	}
	public void setAdminDeptId(Long adminDeptId) {
		this.adminDeptId = adminDeptId;
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
