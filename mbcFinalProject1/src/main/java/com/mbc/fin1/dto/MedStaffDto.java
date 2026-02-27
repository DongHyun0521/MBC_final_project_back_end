// mbcFinalProject1 - com.mbc.mid.dto - MedStaffDto.java
package com.mbc.fin1.dto;
public class MedStaffDto {
    private Long medStaffId;        // PK
    private Long memId;             // FK
    private String role;            // 직업 (의사/간호사)
    private String licenseNumber;   // 면허번호
    private String status;          // 재직 상태
    private Long medDeptId;         // FK
    private Long spotId;            // FK
    private String createTime;      // 생성일시
    
	public MedStaffDto() {
		super();
	}
	public MedStaffDto(Long medStaffId, Long memId, String role, String licenseNumber, String status, Long medDeptId,
			Long spotId, String createTime) {
		super();
		this.medStaffId = medStaffId;
		this.memId = memId;
		this.role = role;
		this.licenseNumber = licenseNumber;
		this.status = status;
		this.medDeptId = medDeptId;
		this.spotId = spotId;
		this.createTime = createTime;
	}
	public Long getMedStaffId() {
		return medStaffId;
	}
	public void setMedStaffId(Long medStaffId) {
		this.medStaffId = medStaffId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getLicenseNumber() {
		return licenseNumber;
	}
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getMedDeptId() {
		return medDeptId;
	}
	public void setMedDeptId(Long medDeptId) {
		this.medDeptId = medDeptId;
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