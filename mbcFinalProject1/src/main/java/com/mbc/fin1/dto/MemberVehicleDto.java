// mbcFinalProject1 - com.mbc.mid.dto - MemberVehicleDto.java
package com.mbc.fin1.dto;
public class MemberVehicleDto {
    private Long vehicleId;     // PK
    private Long memId;         // FK
    private String vehicleNum;  // 차량 번호
    private String vehicleType; // 차종
    private String fuelType;    // 연료
    private String createTime;  // 등록 일시
    
	public MemberVehicleDto() {
		super();
	}
	public MemberVehicleDto(Long vehicleId, Long memId, String vehicleNum, String vehicleType, String fuelType,
			String createTime) {
		super();
		this.vehicleId = vehicleId;
		this.memId = memId;
		this.vehicleNum = vehicleNum;
		this.vehicleType = vehicleType;
		this.fuelType = fuelType;
		this.createTime = createTime;
	}
	public Long getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public String getVehicleNum() {
		return vehicleNum;
	}
	public void setVehicleNum(String vehicleNum) {
		this.vehicleNum = vehicleNum;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getFuelType() {
		return fuelType;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
    
}