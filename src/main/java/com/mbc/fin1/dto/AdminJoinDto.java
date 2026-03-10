// mbcFinalProject1 - com.mbc.fin1.dto - AdminJoinDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminJoinDto {
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
    private String rank;            // 직급
    private String empNumber;       // 사번
    private Long adminDeptId;       // FK
    private Long spotId;            // FK
}