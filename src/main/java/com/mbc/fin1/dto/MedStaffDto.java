// mbcFinalProject1 - com.mbc.fin1.dto - MedStaffDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedStaffDto {
    private Long medStaffId;        // PK
    private Long memId;             // FK
    private String role;            // 직업 (의사/간호사)
    private String licenseNumber;   // 면허번호
    private String status;          // 재직 상태
    private Long medDeptId;         // FK
    private Long spotId;            // FK
    private String createTime;      // 생성일시
}