// mbcFinalProject1 - com.mbc.mid.dto - FaqDto.java
package com.mbc.fin1.dto;

public class FaqDto {
    private Long faqId;             // PK
    private Long adminStaffId;      // FK
    private String category;        // 카테고리
    private String title;           // 제목
    private String content;         // 내용
    private String writeDate;       // 작성일
    private Integer del;			// 삭제 여부
    
	public FaqDto() {
		super();
	}
	public FaqDto(Long faqId, Long adminStaffId, String category, String title, String content, String writeDate, Integer del) {
		super();
		this.faqId = faqId;
		this.adminStaffId = adminStaffId;
		this.category = category;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;
		this.del = del;
	}
	public Long getFaqId() {
		return faqId;
	}
	public void setFaqId(Long faqId) {
		this.faqId = faqId;
	}
	public Long getAdminStaffId() {
		return adminStaffId;
	}
	public void setAdminStaffId(Long adminStaffId) {
		this.adminStaffId = adminStaffId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	public Integer getDel() {
		return del;
	}
	public void setDel(Integer del) {
		this.del = del;
	}
}
