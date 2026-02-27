// mbcFinalProject1 - com.mbc.mid.dto - MemDto.java
package com.mbc.fin1.dto;

public class MemDto {
    private Long memId;             // PK
    private String id;              // 아이디
    private String password;        // 비밀번호
    private String name;            // 이름
    private Integer birthday;       // 생년월일 (숫자 8자리)
    private Integer gender;         // 성별 (1:남, 2:여)
    private String address;         // 주소
    private String addressDetail;   // 상세 주소
    private String phoneNumber;     // 전화번호
    private String email;           // 이메일
    private Integer del;            // 탈퇴 여부
    private String createTime;      // 가입 일시
    
    public MemDto() {
		super();
	}
	public MemDto(Long memId, String id, String password, String name, Integer birthday, Integer gender, String address,
			String addressDetail, String phoneNumber, String email, Integer del, String createTime) {
		super();
		this.memId = memId;
		this.id = id;
		this.password = password;
		this.name = name;
		this.birthday = birthday;
		this.gender = gender;
		this.address = address;
		this.addressDetail = addressDetail;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.del = del;
		this.createTime = createTime;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
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
	public Integer getDel() {
		return del;
	}
	public void setDel(Integer del) {
		this.del = del;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}