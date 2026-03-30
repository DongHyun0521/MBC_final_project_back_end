// mbcFinalProject1 - com.mbc.fin1.service - MedService.java
package com.mbc.fin1.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.fin1.dao.MedDao;
import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dao.ParkingPredictionDao; // 💡 주차 예측 DAO 임포트
import com.mbc.fin1.dto.MedStaffDto;
import com.mbc.fin1.dto.MedStaffJoinDto;
import com.mbc.fin1.dto.MemDto;
import com.mbc.fin1.dto.ReservationDto;

@Service
@Transactional
public class MedService {
	
    @Autowired
    private MedDao medDao;
    
    @Autowired
    private MemDao memDao;

    // 💡 [추가] 예약 변동 시 예측 차트를 리셋하기 위해 주차 예측 DAO 주입
    @Autowired
    private ParkingPredictionDao predictionDao;

    // 의료진 회원 가입
    public void registerMedStaff(MedStaffJoinDto joinDto) {
    	System.out.println("=> MedService: registerMedStaff | "+ new Date());
    	
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

        MedStaffDto staffDto = new MedStaffDto();
        staffDto.setMemId(memDto.getMemId());
        staffDto.setRole(joinDto.getRole());
        staffDto.setLicenseNumber(joinDto.getLicenseNumber());
        staffDto.setMedDeptId(joinDto.getMedDeptId());
        staffDto.setSpotId(joinDto.getSpotId());
        staffDto.setStatus("재직");

        medDao.addMedStaff(staffDto);
    }
    
    // 의사인지 확인
    public boolean isDoctor(Long memId) {
    	System.out.println("=> MedService: isDoctor | "+ new Date());
    	
        return medDao.checkDoctorCount(memId) > 0;
    }
    
    // ========== 진료 예약 ==========

    // 예약하기
    public void createReservation(ReservationDto resDto) {
    	System.out.println("=> MedService: createReservation | "+ new Date());
    	
    	if (resDto.getReservationStatus() == null || resDto.getReservationStatus().isEmpty()) {
            resDto.setReservationStatus("예약");
        }
        medDao.createReservation(resDto);
        
        // 💡 [트리거] 새로운 예약이 생겼으므로 해당 날짜의 주차 예측 차트 폭파!
        if (resDto.getReservationDate() != null) {
            predictionDao.deletePredictionsByDate(resDto.getReservationDate());
        }
    }
    
    // 회원이 예약 취소하기 (예약->취소)
    public int cancelReservation(Long reservationId, Long memId) {
    	System.out.println("=> MedService: cancelReservation | "+ new Date());
    	
        LocalDate rDate = medDao.getReservationDateById(reservationId);					// 회원 예약 취소 전, 예약 날짜 찾기 
        int result = medDao.cancelReservation(reservationId, memId);					// 회원 예약 취소하기
        if (result > 0 && rDate != null) predictionDao.deletePredictionsByDate(rDate);	// 회원 예약 취소 시, 해당 날짜 예약 그래프 삭제
        
        return result;
    }
    
    // 의사가 예약 강제 취소하기 (예약->취소)
    public int cancelReservationByDoctor(Long reservationId, Long memId) {
    	System.out.println("=> MedService: cancelReservationByDoctor | "+ new Date());
    	
        Long doctorId = medDao.getMedStaffIdByMemId(memId);
        if (doctorId == null) return 0; 

        LocalDate rDate = medDao.getReservationDateById(reservationId);					// 의사 강제 예약 취소 전, 예약 날짜 찾기 
        int result = medDao.cancelReservationByDoctor(reservationId, doctorId);			// 의사 강제 예약 취소하기
        if (result > 0 && rDate != null) predictionDao.deletePredictionsByDate(rDate);	// 의사 강제 예약 취소 시, 해당 날짜 예약 그래프 삭제
        
        return result;
    }
    
    // 의사가 예약 완료하기 (예약->완료)
    public int completeReservation(Long reservationId, Long memId) {
    	System.out.println("=> MedService: completeReservation | "+ new Date());
    	
        Long doctorId = medDao.getMedStaffIdByMemId(memId);
        if (doctorId == null) return 0;

        LocalDate rDate = medDao.getReservationDateById(reservationId);					// 의사 예약 완료 전, 예약 날짜 찾기 
        int result = medDao.completeReservation(reservationId, doctorId);				// 의사 예약 완료하기
        if (result > 0 && rDate != null) predictionDao.deletePredictionsByDate(rDate);	// 의사 예약 완료 시, 해당 날짜 예약 그래프 삭제
        
        return result;
    }

    // 자동으로 예약 미방문으로 바꾸기 (예약->미방문)
    public int processNoShowReservations() {
    	System.out.println("=> MedService: processNoShowReservations | "+ new Date());
    	
        List<Long> noShowIds = medDao.findNoShowReservations();
        if (noShowIds != null && !noShowIds.isEmpty()) {
            int result = medDao.updateNoShowStatus(noShowIds);						// 에약 미방문 처리하기
            if (result > 0) predictionDao.deletePredictionsByDate(LocalDate.now());	// 예약 미방문 시, 오늘 예약 그래프 삭제
            
            return result;
        }
        return 0;
    }
    
    // ========== 의료 관련 목록 ==========
    
    // 전체 의사 목록
    public List<Map<String, Object>> getAllDoctors() {
    	System.out.println("=> MedService: getAllDoctors | "+ new Date());
    	
        return medDao.getAllDoctors();
    }

    // 전체 의료 부서 목록
    public List<Map<String, Object>> getAllMedDepts() {
    	System.out.println("=> MedService: getAllMedDepts | "+ new Date());
    	
    	return medDao.getAllMedDepts();
    }
    
    // 의료 부서별 의사 목록
    public List<Map<String, Object>> getDoctorListByDept(Long medDeptId) {
    	System.out.println("=> MedService: getDoctorListByDept | "+ new Date());
    	
    	return medDao.getDoctorListByDept(medDeptId);
    }
    
    // ========== 예약 목록 ==========
    
    // 회원별 예약 목록
    public List<Map<String, Object>> getReservationsByMember(Long memId) {
    	System.out.println("=> MedService: getReservationsByMember | "+ new Date());
    	
    	return medDao.getReservationsByMember(memId);
    }
    
    // 의사별 예약 목록
    public List<Map<String, Object>> getReservationsByDoctor(Long doctorId) {
    	System.out.println("=> MedService: getReservationsByDoctor | "+ new Date());
    	
    	return medDao.getReservationsByDoctor(doctorId);
    }
}