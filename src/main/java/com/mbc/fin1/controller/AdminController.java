// mbcFinalProject1 - com.mbc.fin1.controller - AdminController.java
package com.mbc.fin1.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mbc.fin1.dto.AdminStaffJoinDto;
import com.mbc.fin1.dto.FaqDto;
import com.mbc.fin1.dto.HealthStoryDto;
import com.mbc.fin1.dto.MemDto;
import com.mbc.fin1.dto.NoticeDto;
import com.mbc.fin1.dto.VocDto;
import com.mbc.fin1.service.AdminService;
import com.mbc.fin1.service.MemService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired 
    private AdminService adminService;
    
    @Autowired 
    private MemService memService;

    // 행정직 회원 가입
    @PostMapping("/staff/register")
    public String registerAdminStaff(@RequestBody AdminStaffJoinDto joinDto) {
    	System.out.println("=> AdminController: registerAdminStaff | "+ new Date());
        try {
            adminService.registerAdminStaff(joinDto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 행정 부서 전체 목록
    @GetMapping("/dept/list")
    public List<Map<String, Object>> getAllAdminDepts() {
        System.out.println("=> AdminController: getAllAdminDepts | " + new Date());
        return adminService.getAllAdminDepts();
    }
    
    //
    @GetMapping("/my-info")
    public Map<String, Object> getMyAdminInfo(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        
        if (loginId == null || member == null || !adminService.isAdmin(member.getMemId())) {
            return null; // 관리자 아님
        }

        // 부서명 조회
        String deptName = adminService.getAdminDeptName(member.getMemId());
        boolean isWonmu = (deptName != null && deptName.contains("원무"));
        boolean isPr = (deptName != null && deptName.contains("홍보"));
        
        // [최종프로젝트 추가] 대시보드 접속 가능 부서인지 확인 (주차, 시설, 보안)
        boolean isDashboardAllowed = (deptName != null && 
            (deptName.contains("주차") || deptName.contains("시설") || deptName.contains("보안")));

        // 프론트엔드로 보낼 정보
        return Map.of(
            "isAdmin", true,
            "deptName", deptName != null ? deptName : "",
            "isWonmu", isWonmu,
            "isPr", isPr,
            "isDashboardAllowed", isDashboardAllowed
        );
    }
    
    // ========== 공지사항 ==========
    
    // 공지사항 전체 목록
    @GetMapping("/notice/list")
    public List<NoticeDto> getNoticeList() {
    	System.out.println("=> AdminController: getNoticeList | "+ new Date());
        return adminService.getAllNoticeList();
    }
    
    // 공지사항 상세 보기
    @GetMapping("/notice/detail/{noticeId}")
    public NoticeDto getNoticeDetail(@PathVariable Long noticeId, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("=> AdminController: getNoticeDetail | " + new Date());

        // 쿠키 확인
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        
        // 쿠키가 이미 있을 경우
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewed_notice_" + noticeId)) {
                    oldCookie = cookie;
                    break;
                }
            }
        }
        // 쿠키가 없을 경우
        if (oldCookie == null) {
            adminService.increaseNoticeReadCount(noticeId);
            Cookie newCookie = new Cookie("viewed_notice_" + noticeId, "true");
            newCookie.setPath("/");
            newCookie.setMaxAge(24 * 60 * 60);	// 24시간 (단위: 초)
            response.addCookie(newCookie);
        }
        return adminService.getNoticeDetail(noticeId);
    }

    // 공지사항 작성 (행정원무만 가능)
    @PostMapping("/notice/write")
    public String writeNotice(@ModelAttribute NoticeDto noticeDto, HttpSession session) {
    	System.out.println("=> AdminController: writeNotice | "+ new Date());
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        
        // 권한 체크
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";
        
        try {
            // Service에서 파일 저장 처리함
            adminService.addNotice(noticeDto, member.getMemId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 공지사항 수정 (행정원무만 가능)
    @PostMapping("/notice/update") 
    public String updateNotice(@ModelAttribute NoticeDto noticeDto, HttpSession session) {
        System.out.println("=> AdminController: updateNotice | "+ new Date());
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";
        try {
            return adminService.updateNotice(noticeDto) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 공지사항 삭제 (del=1) (행정원무만 가능)
    @PutMapping("/notice/delete/{noticeId}")
    public String deleteNotice(@PathVariable Long noticeId, HttpSession session) {
        System.out.println("=> AdminController: deleteNotice | "+ new Date());
        // 로그인상태 & 회원 & 행정직인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";
        
        try {
            return adminService.deleteNotice(noticeId) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // ========== FAQ ==========
    
    // FAQ 전체/카테고리별 목록 (누구나)
    @GetMapping("/faq/list")
    public List<FaqDto> getFaqList(@RequestParam(required = false) String category) {
    	System.out.println("=> AdminController: getFaqList | "+ new Date());
        return adminService.getFaqList(category);
    }

    // FAQ 작성 (행정원무만 가능)
    @PostMapping("/faq/write")
    public String writeFaq(@RequestBody FaqDto faqDto, HttpSession session) {
    	System.out.println("=> AdminController: writeFaq | "+ new Date());
    	// 로그인상태 & 회원 & 행정원무 인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";
        
        try {
            adminService.addFaq(faqDto, member.getMemId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // FAQ 수정 (행정원무만 가능)
    @PutMapping("/faq/update")
    public String updateFaq(@RequestBody FaqDto faqDto, HttpSession session) {
    	System.out.println("=> AdminController: updateFaq | "+ new Date());
    	// 로그인상태 & 회원 & 행정직인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";
        
        try {
            return adminService.updateFaq(faqDto) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // FAQ 삭제 (del=1) (행정원무만 가능)
    @PutMapping("/faq/delete/{faqId}")
    public String deleteFaq(@PathVariable Long faqId, HttpSession session) {
    	System.out.println("=> AdminController: deleteFaq | "+ new Date());
    	// 로그인상태 & 회원 & 행정직인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";
        
        try {
            return adminService.deleteFaq(faqId) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // ========== 고객의소리 ==========
    
    // 고객의소리 목록 (전체/미답변/답변완료/삭제)
    @GetMapping("/voc/list")
    public List<VocDto> getVocList(@RequestParam(required = false) String filter, HttpSession session) {
        System.out.println("=> AdminController: getVocList | "+ new Date());
        // 로그인여부 & 회원 & 행정 인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isAdmin(member.getMemId())) return null;

        boolean WonMu = adminService.isWonMu(member.getMemId());
        if (WonMu) {}				// 행정 중 원무
        else filter = "answered";	// 행정 중 원무 아님
        
        return adminService.getAllVocList(filter);
    }
    
    // 고객의소리 상세 보기
    @GetMapping("/voc/detail/{vocId}")
    public VocDto getVocDetail(@PathVariable Long vocId, HttpSession session) {
        System.out.println("=> AdminController: getVocDetail | " + new Date());
        // 로그인여부 & 회원 & 행정 인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isAdmin(member.getMemId())) return null;

        // 빈 글인지 확인
        VocDto vocDto = adminService.getVocDetail(vocId);
        if (vocDto == null) return null;

        boolean WonMu = adminService.isWonMu(member.getMemId());
        if (!WonMu) {	// 행정 중 원무 아님
            if (vocDto.getDel() == 1 || !vocDto.isAnswerStatus())
            	return null;
        }
        return adminService.getVocDetail(vocId);	// 행정 중 원무
    }
    
    // 고객의소리 강제 삭제 (del=1) (행정원무만 가능)
    @DeleteMapping("/voc/delete/{vocId}")
    public String deleteVoc(@PathVariable Long vocId, HttpSession session) {
    	// 로그인여부 & 회원 & 행정원무 인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";

        try {
            return adminService.deleteVocByAdmin(vocId) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 고객의소리 복구 (del=0) (행정원무만 가능)
    @PutMapping("/voc/restore/{vocId}")
    public String restoreVoc(@PathVariable Long vocId, HttpSession session) {
        System.out.println("=> AdminController: restoreVoc | "+ new Date());
        // 로그인여부 & 회원 & 행정원무 인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";

        try {
            return adminService.restoreVoc(vocId) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 고객의소리 답글 작성 (행정원무만 가능)
    @PostMapping("/voc/reply")
    public String writeReply(@RequestBody VocDto vocDto, HttpSession session) {
        System.out.println("=> AdminController: writeReply | "+ new Date());
        // 로그인여부 & 회원 & 행정원무 인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";

        try {
            return adminService.addReply(vocDto, member.getMemId()) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 고객의소리 답글 수정 (행정원무만 가능)
    @PutMapping("/voc/reply")
    public String updateReply(@RequestBody VocDto vocDto, HttpSession session) {
        System.out.println("=> AdminController: updateReply | "+ new Date());
        // 로그인여부 & 회원 & 행정원무 인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";

        try {
            return adminService.updateReply(vocDto, member.getMemId()) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 고객의소리 답글 삭제 (NULL)
    @DeleteMapping("/voc/reply/{vocId}")
    public String deleteReply(@PathVariable Long vocId, HttpSession session) {
        System.out.println("=> AdminController: deleteReply | "+ new Date());
        // 로그인여부 & 회원 & 행정원무 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isWonMu(member.getMemId())) return "fail";

        try {
            return adminService.deleteReply(vocId) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // ========== 건강이야기 ==========
    
    // 건강이야기 목록
    @GetMapping("/health/list")
    public List<HealthStoryDto> getHealthStoryList() {
        System.out.println("=> AdminController: getHealthStoryList | " + new Date());
        return adminService.getAllHealthStories();
    }
    
    // 건강이야기 상세 보기
    @GetMapping("/health/detail/{healthStoryId}")
    public HealthStoryDto getHealthStoryDetail(@PathVariable Long healthStoryId, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("=> AdminController: getHealthStoryDetail | " + new Date());

        // 쿠키 확인
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();

        // 쿠키가 이미 있을 경우
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewed_health_" + healthStoryId)) {
                    oldCookie = cookie;
                    break;
                }
            }
        }
        // 쿠키가 없을 경우
        if (oldCookie == null) {
            adminService.increaseHealthStoryReadCount(healthStoryId);
            Cookie newCookie = new Cookie("viewed_health_" + healthStoryId, "true");
            newCookie.setPath("/");
            newCookie.setMaxAge(24 * 60 * 60); // 24시간 유지
            response.addCookie(newCookie);
        }
        return adminService.getHealthStoryDetail(healthStoryId);
    }
    
    // 건강이야기 작성
    @PostMapping("/health/write")
    public String writeHealthStory(@ModelAttribute HealthStoryDto dto, HttpSession session) {
        System.out.println("=> AdminController: writeHealthStory | " + new Date());
        // 로그인여부 & 회원 & 행정원무 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !adminService.isAdmin(member.getMemId()) || !adminService.isPr(member.getMemId())) return "fail";

        try {
            adminService.addHealthStory(dto, member.getMemId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 건강이야기 수정
    @PostMapping("/health/update")
    public String updateHealthStory(@ModelAttribute HealthStoryDto dto, HttpSession session) {
        System.out.println("=> AdminController: updateHealthStory | " + new Date());
        
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        
        if (loginId == null || member == null || !adminService.isAdmin(member.getMemId()) || !adminService.isPr(member.getMemId())) return "fail";

        try {
            return adminService.updateHealthStory(dto) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 건강이야기 삭제 (del=1)
    @PutMapping("/health/delete/{healthStoryId}")
    public String deleteHealthStory(@PathVariable Long healthStoryId, HttpSession session) {
        System.out.println("=> AdminController: deleteHealthStory | " + new Date());
        
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        
        if (loginId == null || member == null || !adminService.isAdmin(member.getMemId()) || !adminService.isPr(member.getMemId()))
        	return "fail";

        try {
            return adminService.deleteHealthStory(healthStoryId) > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // =============================================================================
    // [최종 프로젝트 추가] 관리자 대시보드 관리 인증
    // 대시보드 사원번호 찾기
    @PostMapping("/find-id")
    public Map<String, Object> findDashboardAdminId(@RequestBody Map<String, String> params) {
        System.out.println("=> AdminController: findDashboardAdminId | " + new Date());
        try {
            // Service를 통해 DB에서 사원번호(empNumber) 조회
            String empId = adminService.findAdminEmpId(params);
            
            if (empId != null && !empId.isEmpty()) {
                return Map.of("is_success", true, "emp_id", empId);
            }
            return Map.of("is_success", false, "message", "일치하는 사원 정보가 없습니다");
            
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("is_success", false, "message", "서버 오류가 발생했습니다");
        }
    }
    
    // 대시보드 전용 로그인
    @PostMapping("/dashboard-login")
    public Map<String, Object> loginDashboardAdmin(@RequestBody Map<String, String> params, HttpSession session) {
        System.out.println("=> AdminController: loginDashboardAdmin | " + new Date());
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> adminData = adminService.verifyAdminAndGetAdminInfo(params);

            if (adminData != null) {
            	System.out.println("=> DB에서 넘어온 데이터 확인: " + adminData);
            	
                session.setAttribute("loginId", adminData.get("loginId"));
                
                result.put("is_success", true);
                result.put("admin_name", adminData.get("adminname"));
                
                return result;
            }
            
            // 데이터가 없을 때
            result.put("is_success", false);
            result.put("message", "부서 또는 사원번호가 일치하지 않습니다");
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("is_success", false);
            result.put("message", "로그인 처리 중 오류 발생");
            return result;
        }
    }
    
}