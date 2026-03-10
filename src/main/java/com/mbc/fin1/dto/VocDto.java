// mbcFinalProject1 - com.mbc.fin1.dto - VocDto.java
package com.mbc.fin1.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VocDto {
    private Long vocId;					// PK
    private Long memId;					// FK
    private String title;				// 제목
    private String content;				// 내용
    private String uploadImg;			// DB에 저장될 이미지 경로 (/images/abc.jpg)
    private String writeDate;			// 작성일시
    private MultipartFile uploadFile;	// 프론트에서 보낸 파일을 받음 (DB 저장X)
    
    private Long adminStaffId;		// FK
    private String answerContent;	// 답변 내용
    private String answerWriteDate;	// 답변 작성일시
    private boolean answerStatus;	// 답변 여부
    
    private Integer del;		// 삭제 여부
    private String deleteDate;	// 삭제 버튼 눌린 시간
    
    private String writerName;		// JOIN 결과 담기용
    private String adminDeptName;	// JOIN 결과 담기용
}