// mbcFinalProject1 - com.mbc.mid.dto - NoticeDto.java
package com.mbc.fin1.dto;

import org.springframework.web.multipart.MultipartFile;

public class NoticeDto {
    private Long noticeId;          // PK
    private Long adminStaffId;      // FK
    private Boolean topFix;         // 상단 고정 여부
    private String title;           // 제목
    private String content;         // 내용
    private String thumbnailImg;	// DB에 저장될 이미지 경로 (/images/abc.jpg)
    private String writeDate;       // 작성일시
    private Integer readCount;      // 조회수
    private Integer del;			// 삭제 여부
    private MultipartFile uploadFile;	// 프론트에서 보낸 파일을 받음 (DB 저장X)
    
	public NoticeDto() {
		super();
	}
	@Override
	public String toString() {
		return "NoticeDto [noticeId=" + noticeId + ", adminStaffId=" + adminStaffId + ", topFix=" + topFix + ", title="
				+ title + ", content=" + content + ", thumbnailImg=" + thumbnailImg + ", writeDate=" + writeDate
				+ ", readCount=" + readCount + ", del=" + del + ", uploadFile=" + uploadFile + "]";
	}
	public Long getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}
	public Long getAdminStaffId() {
		return adminStaffId;
	}
	public void setAdminStaffId(Long adminStaffId) {
		this.adminStaffId = adminStaffId;
	}
	public Boolean getTopFix() {
		return topFix;
	}
	public void setTopFix(Boolean topFix) {
		this.topFix = topFix;
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
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	public Integer getReadCount() {
		return readCount;
	}
	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}
	public Integer getDel() {
		return del;
	}
	public void setDel(Integer del) {
		this.del = del;
	}
	public MultipartFile getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}
}