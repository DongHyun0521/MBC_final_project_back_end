// mbcFinalProject1 - com.mbc.mid.dto - AdminStaffDto.java
package com.mbc.fin1.dto;

public class AdminStaffDto {
    private Long adminStaffId;      // PK
    private Long memId;             // FK
    private String rank;            // 직급
    private String empNumber;       // 사번
    private String status;          // 재직 상태
    private Long adminDeptId;       // FK
    private Long spotId;            // FK
    private String createTime;      // 생성일시
    
	public AdminStaffDto() {
		super();
	}
	public AdminStaffDto(Long adminStaffId, Long memId, String rank, String empNumber, String status, Long adminDeptId,
			Long spotId, String createTime) {
		super();
		this.adminStaffId = adminStaffId;
		this.memId = memId;
		this.rank = rank;
		this.empNumber = empNumber;
		this.status = status;
		this.adminDeptId = adminDeptId;
		this.spotId = spotId;
		this.createTime = createTime;
	}
	public Long getAdminStaffId() {
		return adminStaffId;
	}
	public void setAdminStaffId(Long adminStaffId) {
		this.adminStaffId = adminStaffId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getEmpNumber() {
		return empNumber;
	}
	public void setEmpNumber(String empNumber) {
		this.empNumber = empNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getAdminDeptId() {
		return adminDeptId;
	}
	public void setAdminDeptId(Long adminDeptId) {
		this.adminDeptId = adminDeptId;
	}
	public Long getSpotId() {
		return spotId;
	}
	public void setSpotId(Long spotId) {
		this.spotId = spotId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
    
}
