// mbcFinalProject1 - com.mbc.mid.controller - MemController.java
package com.mbc.fin1.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.fin1.dto.MemDto;
import com.mbc.fin1.dto.MemberVehicleDto;
import com.mbc.fin1.dto.VocDto;
import com.mbc.fin1.service.MemService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/member")
//@CrossOrigin(origins = "http://localhost:5173")
public class MemController {

	@Autowired
	private MemService memService;

	// 아이디 중복 확인
	@GetMapping("/idcheck")
	public boolean checkId(@RequestParam String id) {
		System.out.println("=> MemController: checkId | " + new Date());
		return memService.idCheck(id) == 0;
	}

	// 회원 가입
	@PostMapping("/regi")
	public String signUp(@RequestBody MemDto memDto) {
		System.out.println("=> MemController: signUp | " + new Date());
		try {
			memService.addMem(memDto);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	// 회원 탈퇴 (del=1)
	@DeleteMapping("/withdraw")
	public String withdraw(HttpSession session) {
		System.out.println("=> MemController: withdraw | " + new Date());
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return "fail";

		memService.delMem(loginId);
		session.invalidate();
		return "success";
	}

	// 로그인
	@PostMapping("/login")
	public MemDto login(@RequestBody MemDto memDto, HttpSession session) {
		System.out.println("=> MemController: login | " + new Date());
		MemDto loginUser = memService.login(memDto);

		if (loginUser != null) {
			session.setAttribute("loginId", loginUser.getId());
			return loginUser;
		} else {
			return null;
		}
	}

	// 아이디 찾기
	@PostMapping("/find-id")
	public String findId(@RequestBody MemDto memDto) {
		System.out.println("=> MemController: findId | " + new Date());
		return memService.findId(memDto);
	}

	// 로그아웃
	@PostMapping("/logout")
	public String logout(HttpSession session) {
		System.out.println("=> MemController: logout | " + new Date());
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return "fail";

		session.invalidate();
		return "success";
	}

	// ========== 내 정보 ==========

	// 내 정보
	@GetMapping("/mypage")
	public MemDto getMyInfo(HttpSession session) {
		System.out.println("=> MemController: getMyInfo | " + new Date());
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return null;

		return memService.getMemberInfo(loginId);
	}

	// 내 정보 수정
	@PutMapping("/mypageUpdate")
	public String updateInfo(@RequestBody MemDto memDto, HttpSession session) {
		System.out.println("=> MemController: updateInfo | " + new Date());
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return "fail";

		memDto.setId(loginId);
		memService.updateMem(memDto);
		return "success";
	}

	// ========== 내 차량 ==========

	// 내 차량 목록
	@GetMapping("/vehicles")
	public List<MemberVehicleDto> getMyVehicles(HttpSession session) {
		System.out.println("=> MemController: getMyVehicles | " + new Date());
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return null;

		return memService.getMemberVehicleList(loginId);
	}

	// 내 차량 추가
	/*
    @PostMapping("/vehiRegi")
    public String addVehicle(@RequestBody MemberVehicleDto vehicleDto, HttpSession session) {
    	System.out.println("=> MemController: addVehicle | "+ new Date());
    	String loginId = (String) session.getAttribute("loginId");
        MemDto member = memService.getMemberInfo(loginId);
        if (loginId == null || member == null) return "fail";
        
        vehicleDto.setMemId(member.getMemId());
        memService.addVehi(vehicleDto);
        return "success";
    }
    */

	@PostMapping("/vehiRegi")
	public String addVehicle(@RequestBody MemberVehicleDto vehicleDto, HttpSession session) {
		System.out.println("=> MemController: addVehicle | " + new Date());
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return "fail";

		// 차량 목록 가져오기 활용
		List<MemberVehicleDto> myCars = memService.getMemberVehicleList(loginId);

		if (myCars != null) {
			for (MemberVehicleDto car : myCars) {
				if (car.getVehicleNum().equals(vehicleDto.getVehicleNum())) {
					return "duplicate";
				}
			}
		}
		vehicleDto.setMemId(member.getMemId());

		try {
			memService.addVehi(vehicleDto);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	// 내 차량 수정
	@PutMapping("/vehiUpdate")
	public String updateVehicle(@RequestBody MemberVehicleDto vehicleDto, HttpSession session) {
		System.out.println("=> MemController: updateVehicle | " + new Date());
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null)
			return "fail";

		vehicleDto.setMemId(member.getMemId());

		try {
			return memService.updateVehi(vehicleDto) > 0 ? "success" : "fail";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	// 내 차량 삭제 (delete)
	@DeleteMapping("/vehiDelete/{vehicleNum}")
	public String deleteVehicle(@PathVariable String vehicleNum, HttpSession session) {
		System.out.println("=> MemController: deleteVehicle | " + new Date());
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return "fail";

		try {
			return memService.delVehi(vehicleNum, member.getMemId()) > 0 ? "success" : "fail";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	// ========== 고객의소리 ==========

	// 회원별 고객의소리 목록 (전체/미답변/답변완료)
	// /member/voc/list?filter=all (또는 unanswered, answered)
	@GetMapping("/voc/list")
	public List<VocDto> getMyVocList(@RequestParam(required = false) String filter, HttpSession session) {
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return null;

		return memService.getMyVocList(member.getMemId(), filter);
	}

	// 고객의소리 작성
	@PostMapping("/voc/write")
	public String writeVoc(@ModelAttribute VocDto vocDto, HttpSession session) throws Exception {
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return "fail";

		vocDto.setMemId(member.getMemId());

		memService.addVoc(vocDto);
		return "success";
	}

	// 고객의소리 수정 (답변 없는 것만 수정 가능)
	@PutMapping("/voc/update")
	public String updateVoc(@ModelAttribute VocDto vocDto, HttpSession session) throws Exception {
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return "fail";

		vocDto.setMemId(member.getMemId());

		return memService.updateVoc(vocDto) > 0 ? "success" : "fail";
	}

	// 고객의소리 삭제 (답변이 있을 시 같이 삭제)
	@DeleteMapping("/voc/delete/{vocId}")
	public String deleteVoc(@PathVariable Long vocId, HttpSession session) {
		// 로그인상태 & 회원인지 확인
		String loginId = (String) session.getAttribute("loginId");
		MemDto member = memService.getMemberInfo(loginId);
		if (loginId == null || member == null)
			return "fail";

		return memService.deleteVoc(vocId, member.getMemId()) > 0 ? "success" : "fail";
	}
}