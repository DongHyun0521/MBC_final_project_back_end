// middleProject - com.mbc.fin1.service - MedService.java
package com.mbc.fin1.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.fin1.dao.MedDao;
import com.mbc.fin1.dao.MemDao;
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

    // 의료진 회원 가입
    public void registerMedStaff(MedStaffJoinDto joinDto) {
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
        return medDao.checkDoctorCount(memId) > 0;
    }
    
    // ========== 진료 예약 ==========

    // 예약하기
    public void createReservation(ReservationDto resDto) {
    	if (resDto.getReservationStatus() == null || resDto.getReservationStatus().isEmpty()) {
            resDto.setReservationStatus("예약");
        }
        medDao.createReservation(resDto);
    }
    
    // 회원이 예약 취소하기 (예약->취소)
    public int cancelReservation(Long reservationId, Long memId) {
        return medDao.cancelReservation(reservationId, memId);
    }
    
    // 의사가 예약 강제 취소하기 (예약->취소)
    public int cancelReservationByDoctor(Long reservationId, Long memId) {
        Long doctorId = medDao.getMedStaffIdByMemId(memId);
        if (doctorId == null)
        	return 0; 

        return medDao.cancelReservationByDoctor(reservationId, doctorId);
    }
    
    // 의사가 예약 완료하기 (예약->완료)
    public int completeReservation(Long reservationId, Long memId) {
        Long doctorId = medDao.getMedStaffIdByMemId(memId);
        if (doctorId == null)
        	return 0;
        return medDao.completeReservation(reservationId, doctorId);
    }

    // 자동으로 예약 미방문으로 바꾸기 (예약->미방문)
    public int processNoShowReservations() {
        List<Long> noShowIds = medDao.findNoShowReservations();
        if (noShowIds != null && !noShowIds.isEmpty())
            return medDao.updateNoShowStatus(noShowIds);
        return 0;
    }
    
    // ========== 의료 관련 목록 ==========
    
    // 전체 의사 목록
    public List<Map<String, Object>> getAllDoctors() {
        return medDao.getAllDoctors();
    }

    // 전체 의료 부서 목록
    public List<Map<String, Object>> getAllMedDepts() {
    	return medDao.getAllMedDepts();
    }
    
    // 의료 부서별 의사 목록
    public List<Map<String, Object>> getDoctorListByDept(Long medDeptId) {
    	return medDao.getDoctorListByDept(medDeptId);
    }
    
    // ========== 예약 목록 ==========
    
    // 회원별 예약 목록
    public List<Map<String, Object>> getReservationsByMember(Long memId) {
    	return medDao.getReservationsByMember(memId);
    }
    
    // 의사별 예약 목록
    public List<Map<String, Object>> getReservationsByDoctor(Long doctorId) {
    	return medDao.getReservationsByDoctor(doctorId);
    }
}