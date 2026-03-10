// mbcFinalProject1 - com.mbc.fin1.dao - MemDao.java
package com.mbc.fin1.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mbc.fin1.dto.MemDto;
import com.mbc.fin1.dto.MemberVehicleDto;
import com.mbc.fin1.dto.VocDto;

@Mapper
@Repository
public interface MemDao {
    int idCheck(String id);				// 아이디 중복 확인
    void addMem(MemDto memberDto);		// 회원 가입
    void delMem(String id);				// 회원 탈퇴 (del=1)
    MemDto login(MemDto memberDto);		// 로그인
    String findId(MemDto memberDto);	// 아이디 찾기
    
    MemDto getMemberInfo(String id);	// 내 정보
    void updateMem(MemDto memberDto);	// 내 정보 수정

    List<MemberVehicleDto> getMemberVehicleList(String id);								// 내 차량 목록
    void addVehi(MemberVehicleDto vehicleDto);											// 내 차량 추가
    int updateVehi(MemberVehicleDto vehicleDto);										// 내 차량 수정
    int delVehi(@Param("vehicleNum") String vehicleNum,@Param("memId") Long memId);		// 내 차량 삭제 (delete)
    
    List<VocDto> getMyVocList(@Param("memId") Long memId, @Param("filter") String filter);	// 회원별 고객의소리 목록 (전체/미답변/답변완료)
    void addVoc(VocDto vocDto);																// 고객의소리 작성
    int updateVoc(VocDto vocDto);															// 고객의소리 수정 (답변 없는 것만 수정 가능)
    int deleteVoc(@Param("vocId") Long vocId, @Param("memId") Long memId);					// 고객의소리 삭제 (답변이 있을 시 같이 삭제)

    int checkMemberVehicle(String vehicleNum);	// 번호판으로 회원 차량인지 확인
    Long getMemIdByVehicle(String vehicleNum);	// 번호판으로 회원 찾기
}