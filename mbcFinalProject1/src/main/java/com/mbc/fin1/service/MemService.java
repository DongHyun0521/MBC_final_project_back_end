// middleProject - com.mbc.mid.service - MemService.java
package com.mbc.fin1.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dto.MemDto;
import com.mbc.fin1.dto.MemberVehicleDto;
import com.mbc.fin1.dto.VocDto;

@Service
@Transactional
public class MemService {
    @Autowired
    private MemDao memDao;

    // 아이디 중복 확인
    public int idCheck(String id) {
    	return memDao.idCheck(id);
    }
    
    // 회원 가입
    public void addMem(MemDto memDto) {
    	memDao.addMem(memDto);
    }
    
    // 회원 탈퇴 (del=1)
    public void delMem(String id) {
    	memDao.delMem(id);
    }
    
    // 로그인
    public MemDto login(MemDto memDto) {
    	System.out.println("=> MemService: login | "+ new Date());
    	return memDao.login(memDto);
    }
    
    // 아이디 찾기
    public String findId(MemDto memDto) {
    	System.out.println("=> MemService: findId | "+ new Date());
    	return memDao.findId(memDto);
    }
    
    // ========== 내 정보 ==========
    
    // 내 정보
    public MemDto getMemberInfo(String id) {
    	System.out.println("=> MemService: getMemberInfo | "+ new Date());
    	return memDao.getMemberInfo(id);
    }
    
    // 내 정보 수정
    public void updateMem(MemDto memDto) {
    	System.out.println("=> MemService: updateMem | "+ new Date());
    	memDao.updateMem(memDto);
    }

    // ========== 내 차량 ==========
    
    // 내 차량 목록
    public List<MemberVehicleDto> getMemberVehicleList(String id) {
    	System.out.println("=> MemService: getMemberVehicleList | "+ new Date());
    	return memDao.getMemberVehicleList(id);
    }
    
    // 내 차량 추가
    public void addVehi(MemberVehicleDto vehicleDto) {
    	System.out.println("=> MemService: addVehi | "+ new Date());
    	memDao.addVehi(vehicleDto);
    }
    
    // 내 차량 수정
    public int  updateVehi(MemberVehicleDto vehicleDto) {
    	System.out.println("=> MemService: updateVehi | "+ new Date());
    	return memDao.updateVehi(vehicleDto);
    }
    
    // 내 차량 삭제 (delete)
    public int delVehi(String vehicleNum, Long memId) {
    	System.out.println("=> MemService: delVehi | "+ new Date());
        return memDao.delVehi(vehicleNum, memId);
    }
    
    // ========== 고객의소리 ==========
    
    // 회원별 고객의소리 목록 (전체/미답변/답변완료)
    public List<VocDto> getMyVocList(Long memId, String filter) {
        if (filter == null || filter.isEmpty()) filter = "all";
        return memDao.getMyVocList(memId, filter);
    }
    
    // 파일 저장
    private String saveFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        String uploadDir = "images/";
        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();

        String uuid = UUID.randomUUID().toString();
        String saveName = uuid + "_" + file.getOriginalFilename();
        
        file.transferTo(new File(folder.getAbsolutePath() + "/" + saveName));
        
        return "/images/" + saveName;
    }

    // 고객의소리 작성
    public void addVoc(VocDto vocDto) throws Exception {
    	String savedPath = saveFile(vocDto.getUploadFile());
        vocDto.setUploadImg(savedPath);
        
        memDao.addVoc(vocDto);
    }

    // 고객의소리 수정 (답변 없는 것만 수정 가능)
    public int updateVoc(VocDto vocDto) throws Exception {
    	String savedPath = saveFile(vocDto.getUploadFile());
        if (savedPath != null) vocDto.setUploadImg(savedPath);
        
        return memDao.updateVoc(vocDto);
    }

    // 고객의소리 삭제 (답변이 있을 시 같이 삭제)
    public int deleteVoc(Long vocId, Long memId) {
        return memDao.deleteVoc(vocId, memId);
    }
}