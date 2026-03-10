// mbcFinalProject1 - com.mbc.fin1.dto - AdminStaffJoinDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}