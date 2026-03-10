// mbcFinalProject1 - com.mbc.fin1.dto - MedStaffJoinDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedStaffJoinDto {
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

    // MedStaffDto
    private String role;            // 직업 (의사/간호사)
    private String licenseNumber;   // 면허번호
    private Long medDeptId;         // FK
    private Long spotId;            // FK
}