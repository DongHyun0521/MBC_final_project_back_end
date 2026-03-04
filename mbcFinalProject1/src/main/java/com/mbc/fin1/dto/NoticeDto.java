// mbcFinalProject1 - com.mbc.mid.dto - NoticeDto.java
package com.mbc.fin1.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class NoticeDto {
    private Long noticeId;          	// PK
    private Long adminStaffId;      	// FK
    private Boolean topFix;         	// 상단 고정 여부
    private String title;           	// 제목
    private String content;         	// 내용
    private String thumbnailImg;		// DB에 저장될 이미지 경로 (/images/abc.jpg)
    private String writeDate;       	// 작성일시
    private Integer readCount;      	// 조회수
    private Integer del;				// 삭제 여부
    private MultipartFile uploadFile;	// 프론트에서 보낸 파일을 받음 (DB 저장X)
}