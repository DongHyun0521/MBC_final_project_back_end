// mbcFinalProject1 - com.mbc.fin1.dto - HealthStoryDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthStoryDto {
	private Long healthStoryId;			// PK
    private Long adminStaffId;			// FK
    private String title;				// 제목
    private String content;				// 내용
    private String thumbnailImg;		// DB에 저장될 이미지 경로 (/images/abc.jpg)
    private int readCount;				// 조회수
    private LocalDateTime writeDate;	// 작성일
    private int del;					// 삭제 여부
    private MultipartFile uploadFile;	// 프론트에서 보낸 파일을 받음 (DB 저장X)
}