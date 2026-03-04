// mbcFinalProject1 - com.mbc.mid.dto - MemDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}