// mbcFinalProject1 - com.mbc.fin1.dao - AdminDao.java
package com.mbc.fin1.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mbc.fin1.dto.AdminStaffDto;
import com.mbc.fin1.dto.FaqDto;
import com.mbc.fin1.dto.HealthStoryDto;
import com.mbc.fin1.dto.NoticeDto;
import com.mbc.fin1.dto.VocDto;

@Mapper
@Repository
public interface AdminDao {
    void addAdminStaff(AdminStaffDto adminStaffDto);	// 행정직 회원 가입
    int checkAdminCount(Long memId);					// 행정직인지 확인
    Long getAdminStaffIdByMemId(Long memId);			// 회원ID로 행정직인지 확인
    List<Map<String, Object>> getAllAdminDepts();		// 행정 부서 전체 목록
    
    List<NoticeDto> getAllNoticeList();			// 공지사항 전체 목록
    NoticeDto getNoticeDetail(Long noticeId);	// 공지사항 상세 보기
    int increaseNoticeReadCount(Long noticeId);	// 공지사항 조회수 증가
    void addNotice(NoticeDto noticeDto);		// 공지사항 작성
    int updateNotice(NoticeDto noticeDto);		// 공지사항 수정
    int deleteNotice(Long noticeId);			// 공지사항 삭제 (del=1)
    
    List<FaqDto> getAllFaqList();						// FAQ 전체 목록
    List<FaqDto> getFaqListByCategory(String category);	// FAQ 카테고리별 목록
    void addFaq(FaqDto faqDto);							// FAQ 작성
    int updateFaq(FaqDto faqDto);						// FAQ 수정
    int deleteFaq(Long faqId);							// FAQ 삭제
    
    public String getAdminDeptName(Long memId);	// 부서 이름 확인
    
    public boolean isWonMu(Long memId);			// 무슨 부서(원무)인지 확인
    List<VocDto> getAllVocList(String filter);	// 고객의소리 목록 (전체/미답변/답변완료/삭제)
    VocDto getVocDetail(Long vocId);			// 고객의소리 상세 보기
    int deleteVocByAdmin(Long vocId);			// 고객의소리 강제 삭제 (del=1)
    int permanentDeleteVoc();					// 고객의소리 30일 후 자동 삭제 (delete)
    int restoreVoc(Long vocId);					// 고객의소리 복구 (del=0)
    int addReply(VocDto vocDto);				// 고객의소리 답변 작성
    int updateReply(VocDto vocDto);				// 고객의소리 답변 수정
    int deleteReply(Long vocId);				// 고객의소리 답변 삭제
    
    public boolean isPr(Long memId);							// 무슨 부서(홍보)인지 확인
    List<HealthStoryDto> getAllHealthStories();					// 건강이야기 목록
    void insertHealthStory(HealthStoryDto dto);					// 건강이야기 작성
    HealthStoryDto getHealthStoryDetail(Long healthStoryId);	// 건강이야기 상세 보기
    void increaseHealthStoryReadCount(Long healthStoryId);		// 건강이야기 조회수 증가
    int updateHealthStory(HealthStoryDto dto);					// 건강이야기 수정
    int deleteHealthStory(Long healthStoryId);					// 건강이야기 삭제 (del=1)
    
    // =============================================================================
    // [최종 프로젝트 추가] 관리자 대시보드 전용 인증
    String findAdminEmpId(Map<String, String> params);								// 사원번호 찾기 (이름, 부서, 아이디, 이메일)
    Map<String, Object> verifyAdminAndGetAdminInfo(Map<String, String> params);		// 관리자 로그인 인증 (부서, 사원번호)
    
}