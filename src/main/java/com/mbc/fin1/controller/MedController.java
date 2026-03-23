// mbcFinalProject1 - com.mbc.fin1.controller - MedController.java
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
       
       String loginId = (String) session.getAttribute("loginId");
       MemDto member = memService.getMemberInfo(loginId);
       if (loginId == null || member == null) return "fail";
       
       try {
           List<Map<String, Object>> schedule = medService.getReservationsByDoctor(resDto.getDoctorId());
           
           LocalDate targetDate = resDto.getReservationDate();
           LocalTime targetTime = resDto.getReservationTime();
           LocalDateTime targetDateTime = LocalDateTime.of(targetDate, targetTime);

           // 현재시간 기준 1시간 이후부터 예약 가능
           LocalDateTime minBookingTime = LocalDateTime.now().plusHours(1);	
           if (targetDateTime.isBefore(minBookingTime)) return "soon";
           
           for (Map<String, Object> reserved : schedule) {
               String status = String.valueOf(reserved.get("reservation_status"));
               if (!"예약".equals(status)) continue;

               Object dbDateObj = reserved.get("reservation_date");
               Object dbTimeObj = reserved.get("reservation_time");

               if (dbDateObj != null && dbTimeObj != null) {
                   LocalDate dbDate = (dbDateObj instanceof java.sql.Date) ? ((java.sql.Date) dbDateObj).toLocalDate() : LocalDate.parse(dbDateObj.toString());
                   LocalTime dbTime = (dbTimeObj instanceof java.sql.Time) ? ((java.sql.Time) dbTimeObj).toLocalTime() : LocalTime.parse(dbTimeObj.toString());

                   if (dbDate.equals(targetDate) && dbTime.equals(targetTime)) return "duplicate";
               }
           }
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