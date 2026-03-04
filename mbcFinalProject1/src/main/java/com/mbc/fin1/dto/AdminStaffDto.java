// mbcFinalProject1 - com.mbc.mid.dto - AdminStaffDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStaffDto {
    private Long adminStaffId;      // PK
    private Long memId;             // FK
    private String rank;            // 직급
    private String empNumber;       // 사번
    private String status;          // 재직 상태
    private Long adminDeptId;       // FK
    private Long spotId;            // FK
    private String createTime;      // 생성일시
}