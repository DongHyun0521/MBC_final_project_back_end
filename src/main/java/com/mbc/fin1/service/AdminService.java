// mbcFinalProject1 - com.mbc.fin1.service - AdminService.java
package com.mbc.fin1.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.fin1.dao.AdminDao;
import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dto.AdminStaffDto;
import com.mbc.fin1.dto.AdminStaffJoinDto;
import com.mbc.fin1.dto.FaqDto;
import com.mbc.fin1.dto.HealthStoryDto;
import com.mbc.fin1.dto.MemDto;
import com.mbc.fin1.dto.NoticeDto;
import com.mbc.fin1.dto.VocDto;

@Service
@Transactional
public class AdminService {
	
    @Autowired
    private AdminDao adminDao;
    
    @Autowired
    private MemDao memDao;

    // 행정직 회원 가입
    public void registerAdminStaff(AdminStaffJoinDto joinDto) {
    	System.out.println("=> AdminService: registerAdminStaff | "+ new Date());
    	
        MemDto memDto = new MemDto();
        memDto.setId(joinDto.getId());
        memDto.setPassword(joinDto.getPassword());
        memDto.setName(joinDto.getName());
        memDto.setBirthday(joinDto.getBirthday());
        memDto.setGender(joinDto.getGender());
        memDto.setAddress(joinDto.getAddress());
        memDto.setAddressDetail(joinDto.getAddressDetail());
        memDto.setPhoneNumber(joinDto.getPhoneNumber());
        memDto.setEmail(joinDto.getEmail());
        memDto.setDel(0);

        memDao.addMem(memDto);

        AdminStaffDto adminDto = new AdminStaffDto();
        adminDto.setMemId(memDto.getMemId());
        adminDto.setRank(joinDto.getRank());
        adminDto.setEmpNumber(joinDto.getEmpNumber());
        adminDto.setAdminDeptId(joinDto.getAdminDeptId());
        adminDto.setSpotId(joinDto.getSpotId());
        adminDto.setStatus("재직");

        adminDao.addAdminStaff(adminDto);
    }
    
    // 행정직인지 확인
    public boolean isAdmin(Long memId) {
    	System.out.println("=> AdminService: isAdmin | "+ new Date());
    	
        return adminDao.checkAdminCount(memId) > 0;
    }
    
    // 행정 부서 전체 목록
    public List<Map<String, Object>> getAllAdminDepts() {
    	System.out.println("=> AdminService: getAllAdminDepts | "+ new Date());
    	
        return adminDao.getAllAdminDepts();
    }
    
    // 부서 이름 확인
    public String getAdminDeptName(Long memId) {
    	System.out.println("=> AdminService: getAdminDeptName | "+ new Date());
    	
        return adminDao.getAdminDeptName(memId);
    }
    
    // 무슨 부서(원무)인지 확인
    public boolean isWonMu(Long memId) {
    	System.out.println("=> AdminService: isWonMu | "+ new Date());
    	
        String deptName = adminDao.getAdminDeptName(memId);
        return deptName != null && deptName.contains("원무");
    }
    
    // 무슨 부서(홍보)인지 확인
    public boolean isPr(Long memId) {
    	System.out.println("=> AdminService: isPr | "+ new Date());
    	
        String deptName = adminDao.getAdminDeptName(memId);
        return deptName != null && deptName.contains("홍보");
    }
    
    // 파일 저장
    private String saveFile(MultipartFile file) throws Exception {
    	System.out.println("=> AdminService: saveFile | "+ new Date());
    	
        if (file == null || file.isEmpty()) {
            return null; // 파일이 없으면 null 반환
        }
        
        // 프로젝트 루트/images/ 폴더에 저장
        String uploadDir = "images/";
        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();

        String uuid = UUID.randomUUID().toString();
        String saveName = uuid + "_" + file.getOriginalFilename();
        
        file.transferTo(new File(folder.getAbsolutePath() + "/" + saveName));
        
        // DB에 저장할 경로 (/images/...)
        return "/images/" + saveName;
    }
    
    // ========== 공지사항 ==========
    
    // 공지사항 전체 목록
    public List<NoticeDto> getAllNoticeList() {
    	System.out.println("=> AdminService: saveFile | "+ new Date());
    	
        return adminDao.getAllNoticeList();
    }
    
    // 공지사항 상세 보기
    public NoticeDto getNoticeDetail(Long noticeId) {
    	System.out.println("=> AdminService: saveFile | "+ new Date());
    	
        return adminDao.getNoticeDetail(noticeId);
    }
    
    // 공지사항 조회수 증가
    public void increaseNoticeReadCount(Long noticeId) {
    	System.out.println("=> AdminService: saveFile | "+ new Date());
    	
        adminDao.increaseNoticeReadCount(noticeId);
    }
    
    // 공지사항 작성
    public void addNotice(NoticeDto noticeDto, Long memId) throws Exception {
    	System.out.println("=> AdminService: saveFile | "+ new Date());
    	
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        noticeDto.setAdminStaffId(adminStaffId);
        
        // 공통 파일 저장 로직 사용
        String savedPath = saveFile(noticeDto.getUploadFile());
        noticeDto.setThumbnailImg(savedPath); // DTO 필드명에 맞춤
        
        adminDao.addNotice(noticeDto);
    }
    
    // 공지사항 수정
    public int updateNotice(NoticeDto noticeDto) throws Exception {
    	System.out.println("=> AdminService: updateNotice | "+ new Date());
    	
        // 새 파일이 있으면 저장, 없으면 null 리턴
        String savedPath = saveFile(noticeDto.getUploadFile());
        noticeDto.setThumbnailImg(savedPath); // XML에서 null이면 업데이트 안함
        
        return adminDao.updateNotice(noticeDto);
    }
    
    // 공지사항 삭제 (del=1)
    public int deleteNotice(Long noticeId) {
    	System.out.println("=> AdminService: deleteNotice | "+ new Date());
    	
    	return adminDao.deleteNotice(noticeId);
    }
    
    // ========== FAQ ==========
    
    // FAQ 전체/카테고리별 목록
    public List<FaqDto> getFaqList(String category) {
    	System.out.println("=> AdminService: getFaqList | "+ new Date());
    	
        if (category == null || category.isEmpty()) return adminDao.getAllFaqList();
        else return adminDao.getFaqListByCategory(category);
    }

    // FAQ 작성
    public void addFaq(FaqDto faqDto, Long memId) {
    	System.out.println("=> AdminService: addFaq | "+ new Date());
    	
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        faqDto.setAdminStaffId(adminStaffId);
        adminDao.addFaq(faqDto);
    }
    
    // FAQ 수정
    public int updateFaq(FaqDto faqDto) {
    	System.out.println("=> AdminService: updateFaq | "+ new Date());
    	
    	return adminDao.updateFaq(faqDto);
    }
    
    // FAQ 삭제 (del=1)
    public int deleteFaq(Long faqId) {
    	System.out.println("=> AdminService: deleteFaq | "+ new Date());
    	
    	return adminDao.deleteFaq(faqId);
    }
    
    // ========== 고객의소리 ==========
    
    // 고객의소리 목록 (전체/미답변/답변완료/삭제)
    public List<VocDto> getAllVocList(String filter) {
    	System.out.println("=> AdminService: getAllVocList | "+ new Date());
    	
        if (filter == null || filter.isEmpty()) filter = "all";
        return adminDao.getAllVocList(filter);
    }
    
    // 고객의소리 상세 보기
    public VocDto getVocDetail(Long vocId) {
    	System.out.println("=> AdminService: getVocDetail | "+ new Date());
    	
        return adminDao.getVocDetail(vocId);
    }
    
    // 고객의소리 강제 삭제 (del=1)
    public int deleteVocByAdmin(Long vocId) {
    	System.out.println("=> AdminService: deleteVocByAdmin | "+ new Date());
    	
        return adminDao.deleteVocByAdmin(vocId);
    }
    
    // 고객의소리 복구 (del=0)
    public int restoreVoc(Long vocId) {
    	System.out.println("=> AdminService: restoreVoc | "+ new Date());
    	
        return adminDao.restoreVoc(vocId);
    }

    // 고객의소리 답글 작성
    public int addReply(VocDto vocDto, Long memId) {
    	System.out.println("=> AdminService: addReply | "+ new Date());
    	
        Long staffId = adminDao.getAdminStaffIdByMemId(memId);
        vocDto.setAdminStaffId(staffId);
        return adminDao.addReply(vocDto);
    }

    // 고객의소리 답글 수정
    public int updateReply(VocDto vocDto, Long memId) {
    	System.out.println("=> AdminService: updateReply | "+ new Date());
    	
        Long staffId = adminDao.getAdminStaffIdByMemId(memId);
        vocDto.setAdminStaffId(staffId);
        return adminDao.updateReply(vocDto);
    }

    // 고객의소리 답글 삭제 (NULL)
    public int deleteReply(Long vocId) {
    	System.out.println("=> AdminService: deleteReply | "+ new Date());
    	
        return adminDao.deleteReply(vocId);
    }
    
    // ========== 건강이야기 ==========
    
    // 건강이야기 목록
    public List<HealthStoryDto> getAllHealthStories() {
    	System.out.println("=> AdminService: getAllHealthStories | "+ new Date());
    	
        return adminDao.getAllHealthStories();
    }

    // 건강이야기 작성
    public void addHealthStory(HealthStoryDto dto, Long memId) throws Exception {
    	System.out.println("=> AdminService: addHealthStory | "+ new Date());
    	
        Long staffId = adminDao.getAdminStaffIdByMemId(memId);
        dto.setAdminStaffId(staffId);

        String savedPath = saveFile(dto.getUploadFile());
        dto.setThumbnailImg(savedPath);

        adminDao.insertHealthStory(dto);
    }
    
    // 건강이야기 상세 보기
    public HealthStoryDto getHealthStoryDetail(Long healthStoryId) {
    	System.out.println("=> AdminService: getHealthStoryDetail | "+ new Date());
    	
        return adminDao.getHealthStoryDetail(healthStoryId);
    }
    
    // 건강이야기 조회수 증가
    public void increaseHealthStoryReadCount(Long healthStoryId) {
    	System.out.println("=> AdminService: increaseHealthStoryReadCount | "+ new Date());
    	
        adminDao.increaseHealthStoryReadCount(healthStoryId);
    }
    
    // 건강이야기 수정
    public int updateHealthStory(HealthStoryDto dto) throws Exception {
    	System.out.println("=> AdminService: updateHealthStory | "+ new Date());
    	
        String savedPath = saveFile(dto.getUploadFile());
        if (savedPath != null) dto.setThumbnailImg(savedPath);

        return adminDao.updateHealthStory(dto);
    }
    
    // 건강이야기 삭제 (del=1)
    public int deleteHealthStory(Long healthStoryId) {
    	System.out.println("=> AdminService: deleteHealthStory | "+ new Date());
    	
        return adminDao.deleteHealthStory(healthStoryId);
    }
    
    // =============================================================================
    // [최종 프로젝트 추가] 관리자 대시보드 전용 인증 (사원번호 찾기 & 로그인)
    // 사원 번호 찾기
    public String findAdminEmpId(Map<String, String> parmas) {
    	System.out.print("=> AdminService: findAdminEmpId | " + new Date());
    	
    	return adminDao.findAdminEmpId(parmas);
	}
    
    // 관리자 로그인
    public Map<String, Object> verifyAdminAndGetAdminInfo(Map<String, String> params) {
        System.out.println("=> AdminService: verifyAdminAndGetAdminInfo | " + new Date());
        
        return adminDao.verifyAdminAndGetAdminInfo(params);
    }
    
    // 대시보드 통계 (오늘 예약 건수 조회)
    public int getTodayReservationCount() {
        System.out.println("=> AdminService: getTodayReservationCount | " + new Date());
        
        return adminDao.getTodayReservationCount();
    }

	// 대시보드 관리자 계정 목록 조회
	public List<Map<String, Object>> getDashboardAdmins() {
		System.out.println("=> AdminService: getDashboardAdmins | " + new Date());
		return adminDao.getDashboardAdmins();
	}
	
	// 관리자 상태(권한) 변경
    public void updateAdminStatus(Map<String, String> params) {
        System.out.println("=> AdminService: updateAdminStatus | " + new Date());
        adminDao.updateAdminStatus(params);
    }
}