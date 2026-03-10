// mbcFinalProject1 - com.mbc.fin1.dto - FaqDto.java
package com.mbc.fin1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqDto {
    private Long faqId;             // PK
    private Long adminStaffId;      // FK
    private String category;        // 카테고리
    private String title;           // 제목
    private String content;         // 내용
    private String writeDate;       // 작성일
    private Integer del;			// 삭제 여부
}