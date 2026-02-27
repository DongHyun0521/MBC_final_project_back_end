// mbcFinalProject1 - com.mbc.mid.dto - HealthStoryDto.java
package com.mbc.fin1.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

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
    
	public HealthStoryDto() {
		super();
	}
	public HealthStoryDto(Long healthStoryId, Long adminStaffId, String title, String content, String thumbnailImg,
			int readCount, LocalDateTime writeDate, int del, MultipartFile uploadFile) {
		super();
		this.healthStoryId = healthStoryId;
		this.adminStaffId = adminStaffId;
		this.title = title;
		this.content = content;
		this.thumbnailImg = thumbnailImg;
		this.readCount = readCount;
		this.writeDate = writeDate;
		this.del = del;
		this.uploadFile = uploadFile;
	}
	public Long getHealthStoryId() {
		return healthStoryId;
	}
	public void setHealthStoryId(Long healthStoryId) {
		this.healthStoryId = healthStoryId;
	}
	public Long getAdminStaffId() {
		return adminStaffId;
	}
	public void setAdminStaffId(Long adminStaffId) {
		this.adminStaffId = adminStaffId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getThumbnailImg() {
		return thumbnailImg;
	}
	public void setThumbnailImg(String thumbnailImg) {
		this.thumbnailImg = thumbnailImg;
	}
	public int getReadCount() {
		return readCount;
	}
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	public LocalDateTime getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(LocalDateTime writeDate) {
		this.writeDate = writeDate;
	}
	public int getDel() {
		return del;
	}
	public void setDel(int del) {
		this.del = del;
	}
	public MultipartFile getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}
}
