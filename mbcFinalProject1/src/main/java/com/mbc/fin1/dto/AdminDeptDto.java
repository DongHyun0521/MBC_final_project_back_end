// mbcFinalProject1 - com.mbc.mid.dto - AdminDeptDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDeptDto {
	private Long adminDeptId;		// PK
    private String deptName;		// 부서명
    private String deptLocation;	// 위치
    private String deptPhoneNumber;	// 전화번호   
}