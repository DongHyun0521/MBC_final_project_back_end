// mbcFinalProject1 - com.mbc.fin1.dto - OcrResponse.java
package com.mbc.fin1.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    // 💡 추가된 필드 2개
    private Boolean alreadyPaid;        // 결제 완료 여부
    private String allocatedSpot;       // 배정된 주차 자리 (예: "1층 1행 1열")

    // Service 코드에서 에러 안 나도록 만들어둔 맞춤형 생성자
    public OcrResponse(String resultText, String rawText, List<String> debugImages, 
                       String entryTime, String exitTime, Boolean isMember, 
                       Integer parkingFee, Long parkingLogId, Long memId, String allocatedSpot) {
        this.resultText = resultText;
        this.rawText = rawText;
        this.debugImages = debugImages;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.isMember = isMember;
        this.parkingFee = parkingFee;
        this.parkingLogId = parkingLogId;
        this.memId = memId;
        this.alreadyPaid = false; 
        this.allocatedSpot = allocatedSpot; 
    }
}