// mbcFinalProject1 - com.mbc.mid.service - AdminService.java
package com.mbc.fin1.service;

import java.io.File;
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
        return adminDao.checkAdminCount(memId) > 0;
    }
    
    // 행정 부서 전체 목록
    public List<Map<String, Object>> getAllAdminDepts() {
        return adminDao.getAllAdminDepts();
    }
    
    // 부서 이름 확인
    public String getAdminDeptName(Long memId) {
        return adminDao.getAdminDeptName(memId);
    }
    
    // 무슨 부서(원무)인지 확인
    public boolean isWonMu(Long memId) {
        String deptName = adminDao.getAdminDeptName(memId);
        return deptName != null && deptName.contains("원무");
    }
    
    // 무슨 부서(홍보)인지 확인
    public boolean isPr(Long memId) {
        String deptName = adminDao.getAdminDeptName(memId);
        return deptName != null && deptName.contains("홍보");
    }
    
    // 파일 저장
    private String saveFile(MultipartFile file) throws Exception {
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
        return adminDao.getAllNoticeList();
    }
    
    // 공지사항 상세 보기
    public NoticeDto getNoticeDetail(Long noticeId) {
        return adminDao.getNoticeDetail(noticeId);
    }
    
    // 공지사항 조회수 증가
    public void increaseNoticeReadCount(Long noticeId) {
        adminDao.increaseNoticeReadCount(noticeId);
    }
    
    // 공지사항 작성
    public void addNotice(NoticeDto noticeDto, Long memId) throws Exception {
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        noticeDto.setAdminStaffId(adminStaffId);
        
        // 공통 파일 저장 로직 사용
        String savedPath = saveFile(noticeDto.getUploadFile());
        noticeDto.setThumbnailImg(savedPath); // DTO 필드명에 맞춤
        
        adminDao.addNotice(noticeDto);
    }
    
    // 공지사항 수정
    public int updateNotice(NoticeDto noticeDto) throws Exception {
        // 새 파일이 있으면 저장, 없으면 null 리턴
        String savedPath = saveFile(noticeDto.getUploadFile());
        noticeDto.setThumbnailImg(savedPath); // XML에서 null이면 업데이트 안함
        
        return adminDao.updateNotice(noticeDto);
    }
    
    // 공지사항 삭제 (del=1)
    public int deleteNotice(Long noticeId) {
    	return adminDao.deleteNotice(noticeId);
    }
    
    // ========== FAQ ==========
    
    // FAQ 전체/카테고리별 목록
    public List<FaqDto> getFaqList(String category) {
        if (category == null || category.isEmpty()) return adminDao.getAllFaqList();
        else return adminDao.getFaqListByCategory(category);
    }

    // FAQ 작성
    public void addFaq(FaqDto faqDto, Long memId) {
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        faqDto.setAdminStaffId(adminStaffId);
        adminDao.addFaq(faqDto);
    }
    
    // FAQ 수정
    public int updateFaq(FaqDto faqDto) {
    	return adminDao.updateFaq(faqDto);
    }
    
    // FAQ 삭제 (del=1)
    public int deleteFaq(Long faqId) {
    	return adminDao.deleteFaq(faqId);
    }
    
    // ========== 고객의소리 ==========
    
    // 고객의소리 목록 (전체/미답변/답변완료/삭제)
    public List<VocDto> getAllVocList(String filter) {
        if (filter == null || filter.isEmpty()) filter = "all";
        return adminDao.getAllVocList(filter);
    }
    
    // 고객의소리 상세 보기
    public VocDto getVocDetail(Long vocId) {
        return adminDao.getVocDetail(vocId);
    }
    
    // 고객의소리 강제 삭제 (del=1)
    public int deleteVocByAdmin(Long vocId) {
        return adminDao.deleteVocByAdmin(vocId);
    }
    
    // 고객의소리 복구 (del=0)
    public int restoreVoc(Long vocId) {
        return adminDao.restoreVoc(vocId);
    }

    // 고객의소리 답글 작성
    public int addReply(VocDto vocDto, Long memId) {
        Long staffId = adminDao.getAdminStaffIdByMemId(memId);
        vocDto.setAdminStaffId(staffId);
        return adminDao.addReply(vocDto);
    }

    // 고객의소리 답글 수정
    public int updateReply(VocDto vocDto, Long memId) {
        Long staffId = adminDao.getAdminStaffIdByMemId(memId);
        vocDto.setAdminStaffId(staffId);
        return adminDao.updateReply(vocDto);
    }

    // 고객의소리 답글 삭제 (NULL)
    public int deleteReply(Long vocId) {
        return adminDao.deleteReply(vocId);
    }
    
    // ========== 건강이야기 ==========
    
    // 건강이야기 목록
    public List<HealthStoryDto> getAllHealthStories() {
        return adminDao.getAllHealthStories();
    }

    // 건강이야기 작성
    public void addHealthStory(HealthStoryDto dto, Long memId) throws Exception {
        Long staffId = adminDao.getAdminStaffIdByMemId(memId);
        dto.setAdminStaffId(staffId);

        String savedPath = saveFile(dto.getUploadFile());
        dto.setThumbnailImg(savedPath);

        adminDao.insertHealthStory(dto);
    }
    
    // 건강이야기 상세 보기
    public HealthStoryDto getHealthStoryDetail(Long healthStoryId) {
        return adminDao.getHealthStoryDetail(healthStoryId);
    }
    
    // 건강이야기 조회수 증가
    public void increaseHealthStoryReadCount(Long healthStoryId) {
        adminDao.increaseHealthStoryReadCount(healthStoryId);
    }
    
    // 건강이야기 수정
    public int updateHealthStory(HealthStoryDto dto) throws Exception {
        String savedPath = saveFile(dto.getUploadFile());
        if (savedPath != null) dto.setThumbnailImg(savedPath);

        return adminDao.updateHealthStory(dto);
    }
    
    // 건강이야기 삭제 (del=1)
    public int deleteHealthStory(Long healthStoryId) {
        return adminDao.deleteHealthStory(healthStoryId);
    }
}