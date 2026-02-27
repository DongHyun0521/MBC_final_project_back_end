// mbcFinalProject1 - com.mbc.mid.dto - VocDto.java
package com.mbc.fin1.dto;

import org.springframework.web.multipart.MultipartFile;

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
	public VocDto() {
		super();
	}
	public VocDto(Long vocId, Long memId, String title, String content, String uploadImg, String writeDate,
			MultipartFile uploadFile, Long adminStaffId, String answerContent, String answerWriteDate,
			boolean answerStatus, Integer del, String deleteDate, String writerName, String adminDeptName) {
		super();
		this.vocId = vocId;
		this.memId = memId;
		this.title = title;
		this.content = content;
		this.uploadImg = uploadImg;
		this.writeDate = writeDate;
		this.uploadFile = uploadFile;
		this.adminStaffId = adminStaffId;
		this.answerContent = answerContent;
		this.answerWriteDate = answerWriteDate;
		this.answerStatus = answerStatus;
		this.del = del;
		this.deleteDate = deleteDate;
		this.writerName = writerName;
		this.adminDeptName = adminDeptName;
	}
	public Long getVocId() {
		return vocId;
	}
	public void setVocId(Long vocId) {
		this.vocId = vocId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
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
	public String getUploadImg() {
		return uploadImg;
	}
	public void setUploadImg(String uploadImg) {
		this.uploadImg = uploadImg;
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	public MultipartFile getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}
	public Long getAdminStaffId() {
		return adminStaffId;
	}
	public void setAdminStaffId(Long adminStaffId) {
		this.adminStaffId = adminStaffId;
	}
	public String getAnswerContent() {
		return answerContent;
	}
	public void setAnswerContent(String answerContent) {
		this.answerContent = answerContent;
	}
	public String getAnswerWriteDate() {
		return answerWriteDate;
	}
	public void setAnswerWriteDate(String answerWriteDate) {
		this.answerWriteDate = answerWriteDate;
	}
	public boolean getAnswerStatus() {
		return answerStatus;
	}
	public void setAnswerStatus(boolean answerStatus) {
		this.answerStatus = answerStatus;
	}
	public Integer getDel() {
		return del;
	}
	public void setDel(Integer del) {
		this.del = del;
	}
	public String getDeleteDate() {
		return deleteDate;
	}
	public void setDeleteDate(String deleteDate) {
		this.deleteDate = deleteDate;
	}
	public String getWriterName() {
		return writerName;
	}
	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}
	public String getAdminDeptName() {
		return adminDeptName;
	}
	public void setAdminDeptName(String adminDeptName) {
		this.adminDeptName = adminDeptName;
	}
}
