// mbcFinalProject1 - com.mbc.mid.dto - AdminStaffJoinDto.java
package com.mbc.fin1.dto;

public class AdminStaffJoinDto {
    // MemDto
    private String id;
    private String password;
    private String name;
    private Integer birthday;
    private Integer gender;
    private String address;
    private String addressDetail;
    private String phoneNumber;
    private String email;

    // AdminStaffDto
    private String rank;
    private String empNumber;
    private Long adminDeptId;
    private Long spotId;
    
	public AdminStaffJoinDto() {
		super();
	}
	public AdminStaffJoinDto(String id, String password, String name, Integer birthday, Integer gender, String address,
			String addressDetail, String phoneNumber, String email, String rank, String empNumber, Long adminDeptId,
			Long spotId) {
		super();
		this.id = id;
		this.password = password;
		this.name = name;
		this.birthday = birthday;
		this.gender = gender;
		this.address = address;
		this.addressDetail = addressDetail;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.rank = rank;
		this.empNumber = empNumber;
		this.adminDeptId = adminDeptId;
		this.spotId = spotId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getBirthday() {
		return birthday;
	}
	public void setBirthday(Integer birthday) {
		this.birthday = birthday;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
    
}
