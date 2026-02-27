// mbcFinalProject1 - com.mbc.mid.dto - OcrResponse.java
package com.mbc.fin1.dto;

import java.util.List;

public class OcrResponse {
    private String resultText;        	// 차량 번호
    private String rawText;           	// 원본 텍스트
    private List<String> debugImages;	// 이미지
    private String entryTime;			// 입차 시간
    private String exitTime;			// 출차 시간
    private Boolean isMember;			// 회원 여부
    private Integer parkingFee;			// 회원 여부에 따른 주차 요금
    private Long parkingLogId;			// 결제할 주차기록 ID
    private Long memId;					// 회원 ID (비회원: null)
    
    // 추가
    private boolean alreadyPaid;		// 사전정산 완료 여부
    
    public boolean isAlreadyPaid() {
        return alreadyPaid;
    }
    public void setAlreadyPaid(boolean alreadyPaid) {
        this.alreadyPaid = alreadyPaid;
    }
    // 추가 끝
    
	public OcrResponse(String resultText, String rawText, List<String> debugImages, String entryTime, String exitTime, Boolean isMember, Integer parkingFee, Long parkingLogId, Long memId) {
		this.resultText = resultText;
		this.rawText = rawText;
		this.debugImages = debugImages;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.isMember = isMember;
		this.parkingFee = parkingFee;
		this.parkingLogId = parkingLogId;
		this.memId = memId;
	}
	public String getResultText() {
		return resultText;
	}
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}
	public String getRawText() {
		return rawText;
	}
	public void setRawText(String rawText) {
		this.rawText = rawText;
	}
	public List<String> getDebugImages() {
		return debugImages;
	}
	public void setDebugImages(List<String> debugImages) {
		this.debugImages = debugImages;
	}
	public String getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}
	public String getExitTime() {
		return exitTime;
	}
	public void setExitTime(String exitTime) {
		this.exitTime = exitTime;
	}
	public Boolean getIsMember() {
		return isMember;
	}
	public void setIsMember(Boolean isMember) {
		this.isMember = isMember;
	}
	public Integer getParkingFee() {
		return parkingFee;
	}
	public void setParkingFee(Integer parkingFee) {
		this.parkingFee = parkingFee;
	}
	public Long getParkingLogId() {
		return parkingLogId;
	}
	public void setParkingLogId(Long parkingLogId) {
		this.parkingLogId = parkingLogId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	@Override
	public String toString() {
		return "OcrResponse [resultText=" + resultText + ", rawText=" + rawText + ", debugImages=" + debugImages
				+ ", entryTime=" + entryTime + ", exitTime=" + exitTime + ", isMember=" + isMember + ", parkingFee="
				+ parkingFee + ", parkingLogId=" + parkingLogId + ", memId=" + memId + "]";
	}
}