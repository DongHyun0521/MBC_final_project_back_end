// mbcFinalProject1 - com.mbc.fin1.dto - MedDeptDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedDeptDto {
    private Long medDeptId;         // PK
    private String deptName;        // 부서명
    private String deptLocation;    // 위치
    private String deptPhoneNumber; // 전화번호
}