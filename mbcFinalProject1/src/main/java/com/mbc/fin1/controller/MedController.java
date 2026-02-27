// mbcFinalProject1 - com.mbc.mid.controller - MedController.java
package com.mbc.fin1.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mbc.fin1.dto.MedStaffJoinDto;
import com.mbc.fin1.dto.MemDto;
import com.mbc.fin1.dto.ReservationDto;
import com.mbc.fin1.service.MedService;
import com.mbc.fin1.service.MemService;

@RestController
@RequestMapping("/med")
public class MedController {
	
    @Autowired
    private MedService medService;
    
    @Autowired
    private MemService memService;

    // 의료진 회원 가입
    @PostMapping("/staff/register")
    public String registerStaff(@RequestBody MedStaffJoinDto joinDto) {
    	System.out.println("=> MedController: registerStaff | "+ new Date());
        try {
        	medService.registerMedStaff(joinDto);
        	return "success";
        } 
        catch (Exception e) {
        	e.printStackTrace();
        	return "fail";
        }
    }
    
    // ========== 진료 예약 ==========
    
    // 예약하기
    @PostMapping("/reservation")
    public String makeReservation(@RequestBody ReservationDto resDto, HttpSession session) {
       System.out.println("=> MedController: makeReservation | "+ new Date());
       // 1. 로그인 상태 & 회원인지 확인
       String loginId = (String) session.getAttribute("loginId");
       MemDto member = memService.getMemberInfo(loginId);
       if (loginId == null || member == null) return "fail";
       
       try {
    	   List<Map<String, Object>> schedule = medService.getReservationsByDoctor(resDto.getDoctorId());	// 특정 의사 스케줄 목록
           String reqDate = String.valueOf(resDto.getReservationDate());									// YYYYMMDD
           String reqTime = resDto.getReservationTime().toLocalTime().toString().substring(0, 5);			// hh:mm

           DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
           LocalDate targetDate = LocalDate.parse(reqDate, dateFormatter);			// YYYYMMDD -> YYYY년 MM월 DD일
           LocalTime targetTime = LocalTime.parse(reqTime);							// hh:mm -> 오전/후 hh시 mm분
           LocalDateTime targetDateTime = LocalDateTime.of(targetDate, targetTime);	// YYYY년 MM월 DD일 + 오전/후 hh시 mm분

           // .plusMinutes(n) .plusHours(n) .plusDays(n) .plusWeeks(n) .plusMonths(n) .plusYears(n)
           LocalDateTime minBookingTime = LocalDateTime.now().plusHours(1);	// 현재시간 + 1시간
           if (targetDateTime.isBefore(minBookingTime)) return "soon";
           
           for (Map<String, Object> reserved : schedule) {
        	   String status = String.valueOf(reserved.get("reservation_status"));
        	   if (!"예약".equals(status)) continue;	// 예약취소 상태 무시

        	   Object dbDateObj = reserved.get("reservation_date");
        	   Object dbTimeObj = reserved.get("reservation_time");

        	   if (dbDateObj != null && dbTimeObj != null) {
        		   // DB 데이터 날짜 정규화
        		   String dbDate = String.valueOf(dbDateObj).trim();
        		   // ISO 형태 : YYYY-MM-DDThh:mm:ss -> YYYY-MM-DD
        		   if (dbDate.contains("T")) dbDate = dbDate.split("T")[0];
        		   // DB 형태 : YYYY-MM-DD hh:mm:ss -> YYYY-MM-DD
        		   if (dbDate.contains(" ")) dbDate = dbDate.split(" ")[0];
        		   // YYYY-MM-DD -> YYYYMMDD
        		   dbDate = dbDate.replaceAll("-", "");

        		   // DB 데이터 시간 정규화
        		   String dbTime = String.valueOf(dbTimeObj).trim();
        		   // ISO 형태 : YYYY-MM-DDThh:mm:ss -> hh:mm:ss
        		   if (dbTime.contains("T")) dbTime = dbTime.split("T")[1];
        		   // DB 형태 : YYYY-MM-DD hh:mm:ss -> hh:mm:ss
        		   if (dbTime.contains(" ")) dbTime = dbTime.split(" ")[1];
        		   // hh:mm:ss -> hh:mm
        		   dbTime = dbTime.substring(0, 5);

        		   // 이미 예약된 시간
        		   if (dbDate.equals(reqDate) && dbTime.equals(reqTime)) return "duplicate";
        	   }
           }
           // 예약 가능
           resDto.setMemId(member.getMemId());
           medService.createReservation(resDto);
           return "success";
       } catch (Exception e) {
    	   e.printStackTrace();
    	   return "fail";
       }
    }
    
    // 회원이 예약 취소하기 (예약->취소)
    @PutMapping("/reservation/cancel/{reservationId}")
    public String cancelReservation(@PathVariable Long reservationId, HttpSession session) {
        System.out.println("=> MedController: cancelReservation | "+ new Date());
        // 로그인상태 & 회원인지 확인
    	String loginId = (String) session.getAttribute("loginId");
    	MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null) return "fail";
    	
        try {
            int result = medService.cancelReservation(reservationId, member.getMemId());
            if (result > 0) return "success";
            else return "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 의사가 예약 강제 취소하기 (예약->취소)
    @PutMapping("/doctor/reservation/cancel/{reservationId}")
    public String cancelReservationByDoctor(@PathVariable Long reservationId, HttpSession session) {
        System.out.println("=> MedController: cancelReservationByDoctor | " + new Date());
        // 로그인여부 & 회원 & 의사 인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null || !medService.isDoctor(member.getMemId())) return "fail";

        try {
            int result = medService.cancelReservationByDoctor(reservationId, member.getMemId());
            if (result > 0) return "success";
            else return "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 의사가 예약 완료하기 (예약->완료)
    @PutMapping("/doctor/reservation/complete/{reservationId}")
    public String completeReservation(@PathVariable Long reservationId, HttpSession session) {
        System.out.println("=> MedController: completeReservation | " + new Date());
        
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        
        // 의사 권한 체크
        if (loginId == null || member == null || !medService.isDoctor(member.getMemId())) return "fail";

        try {
            int result = medService.completeReservation(reservationId, member.getMemId());
            return result > 0 ? "success" : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // ========== 의료 관련 목록 ==========
    
    // 전체 의사 목록
    @GetMapping("/doctors")
    public List<Map<String, Object>> getAllDoctors() {
    	System.out.println("=> MedController: getAllDoctors | "+ new Date());
        return medService.getAllDoctors();
    }

    // 전체 의료 부서 목록
    @GetMapping("/dept/list")
    public List<Map<String, Object>> getAllDepts() {
    	System.out.println("=> MedController: getAllDepts | "+ new Date());
    	return medService.getAllMedDepts();
    }

    // 의료 부서별 의사 목록
    @GetMapping("/staff/dept/{deptId}")
    public List<Map<String, Object>> getDoctorByDept(@PathVariable Long deptId) {
    	System.out.println("=> MedController: getDoctorByDept | "+ new Date());
    	return medService.getDoctorListByDept(deptId);
    }
    
    // ========== 예약 목록 ==========
    
    // 회원별 예약 목록
    @GetMapping("/my-reservations")
    public List<Map<String, Object>> getMyReservations(HttpSession session) {
    	System.out.println("=> MedController: getMyReservations | "+ new Date());
    	// 로그인상태 & 회원인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null) return null;

        return medService.getReservationsByMember(member.getMemId());
    }
    
    // 의사별 예약 목록
    @GetMapping("/doctor/schedule/{doctorId}")
    public List<Map<String, Object>> getDoctorSchedule(@PathVariable Long doctorId, HttpSession session) {
    	System.out.println("=> MedController: getDoctorSchedule | "+ new Date());
    	// 로그인상태 & 회원인지 확인
        String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null) return null;
        
        return medService.getReservationsByDoctor(doctorId);
    }
}