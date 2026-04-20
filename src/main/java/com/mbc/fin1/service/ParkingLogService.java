// mbcFinalProject1 - com.mbc.fin1.service - ParkingLogService.java
package com.mbc.fin1.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.fin1.dao.MemDao;
import com.mbc.fin1.dao.ParkingLogDao;
import com.mbc.fin1.dao.ParkingSpotDao;
import com.mbc.fin1.dto.ParkingLogDto;
import com.mbc.fin1.dto.ParkingSpotDto;

@Service
@Transactional
public class ParkingLogService {

	@Autowired
	private ParkingLogDao parkingLogDao;
	@Autowired
	private MemDao memDao;
	@Autowired
	private ParkingSpotDao parkingSpotDao;

	// application.properties에서 주입 (프로젝트 루트/images)
	@Value("${app.upload.dir}")
	private String uploadDir;

	// 파이썬 서버 연동
	private final String PYTHON_URL = "http://localhost:8001/license-plates-recognition";

	// 차량 입차 시
	public ParkingLogDto processEntry(MultipartFile file) throws IOException {
		// 파일 바이트를 먼저 보관 (Python 전송 + 이미지 저장 양쪽에 사용)
		byte[] fileBytes = file.getBytes();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new ByteArrayResource(fileBytes) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		});

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(PYTHON_URL, request, Map.class);
		Map<String, Object> aiResult = response.getBody();

		if (aiResult == null || !"success".equals(aiResult.get("status"))) {
			throw new RuntimeException("AI 실패: " + (aiResult != null ? aiResult.get("message") : "응답없음"));
		}

		// DTO 조립
		ParkingLogDto log = new ParkingLogDto();
		log.setLicensePlateCountry((String) aiResult.get("license_plate_country"));
		log.setCountryAccuracy(((Number) aiResult.get("country_accuracy")).doubleValue());
		log.setOcrAccuracy(((Number) aiResult.get("ocr_accuracy")).doubleValue());
		log.setIsEvLicensePlate((Boolean) aiResult.get("is_ev_license_plate"));

		String rawNum = (String) aiResult.get("vehicle_num");
		log.setVehicleNum(rawNum != null ? rawNum.replace(" ", "") : null);

		// OCR 실패 → 이미지 저장 안 함, DB 저장 안 함
		String vNum = log.getVehicleNum();
		if (vNum == null || vNum.isEmpty() || "Unknown".equals(vNum) || "인식불가".equals(vNum)) {
			log.setVehicleNum("인식불가");
			return log;
		}

		// 중복 입차 → 이미지 저장 안 함, DB 저장 안 함
		ParkingLogDto existing = parkingLogDao.selectRecentEntryLog(log.getVehicleNum());
		if (existing != null) {
			existing.setParkingStatus("ALREADY_PARKED");
			return existing;
		}

		// ── 여기까지 통과해야만 이미지 저장 ──
		java.nio.file.Files.createDirectories(java.nio.file.Paths.get(uploadDir, "vehicle"));
		java.nio.file.Files.createDirectories(java.nio.file.Paths.get(uploadDir, "plates"));

		String vFilename = "car_" + UUID.randomUUID().toString().replace("-", "") + ".jpg";
		String pFilename = "plate_" + UUID.randomUUID().toString().replace("-", "") + ".jpg";

		java.nio.file.Files.write(java.nio.file.Paths.get(uploadDir, "vehicle", vFilename), fileBytes);

		String plateBase64 = (String) aiResult.get("plate_img_base64");
		byte[] plateBytes = java.util.Base64.getDecoder().decode(plateBase64);
		java.nio.file.Files.write(java.nio.file.Paths.get(uploadDir, "plates", pFilename), plateBytes);

		log.setVehicleImg("/images/vehicle/" + vFilename);
		log.setLicensePlateImg("/images/plates/" + pFilename);

		// 회원 차량 여부 체크
		log.setIsMember(memDao.checkMemberVehicle(log.getVehicleNum()) > 0);

		// DB 저장
		parkingLogDao.insertEntryLog(log);

		// DB에 저장된 값으로 재조회
		ParkingLogDto saved = parkingLogDao.findById(log.getParkingLogId().intValue());

		// 주차 자리 추천
		ParkingSpotDto spot = parkingSpotDao.findNearestAvailableSpot();
		if (spot != null) {
			saved.setRecommendedSpot(
					spot.getParkingFloor() + "층 " + spot.getParkingRow() + "-" + spot.getParkingColumn());
		}
		return saved;
	}

	// ==================================================================================================================
	// 차량 출차
	// 8004 정산 서버 주소 (출차+결제)
	private final String PYTHON_BRAIN_URL = "http://localhost:8004/api/parking-payment/exit";

	public Map<String, Object> processExit(MultipartFile file) throws IOException {
		// 이미지 데이터 추출
		byte[] fileBytes = file.getBytes();

		// 통신 객체 및 파일 전송용 헤더 설정
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new org.springframework.http.converter.StringHttpMessageConverter(
				java.nio.charset.StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		// ocr용 멀티파트 바디 구성
		MultiValueMap<String, Object> ocrBody = new LinkedMultiValueMap<>();
		ocrBody.add("file", new ByteArrayResource(fileBytes) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		});

		// ocr 요청 엔티티 조립
		HttpEntity<MultiValueMap<String, Object>> ocrReq = new HttpEntity<>(ocrBody, headers);

		// 8001 ocr 엔진 서버 호출 (이미지 텍스트화)
		ResponseEntity<Map> ocrRes = restTemplate.postForEntity(PYTHON_URL, ocrReq, Map.class);
		Map<String, Object> aiResult = ocrRes.getBody();

		// 인식 데이터 추출
		String vehicleNum = (String) aiResult.get("vehicle_num");
		// 나라 정보가 없으면 기본값 "KR"로 설정 (키오스크 언어 전환 안전장치)
		String country = (aiResult.get("license_plate_country") != null)
				? (String) aiResult.get("license_plate_country")
				: "KOR";

		// 차량 번호 공백 제거 및 유효성 검증
		vehicleNum = (vehicleNum != null) ? vehicleNum.replace(" ", "") : "Unknown";

		if ("Unknown".equals(vehicleNum))
			throw new RuntimeException("번호판 인식 실패");

		ParkingLogDto activeLog = parkingLogDao.selectRecentEntryLog(vehicleNum);

		if (activeLog == null) {
			// DB에 입차 기록이 아예 없는 경우 8004까지 갈 필요도 없이 여기서 X
			Map<String, Object> failResult = new java.util.HashMap<>();
			failResult.put("vehicle_num", vehicleNum);
			failResult.put("license_plate_country", country);
			failResult.put("is_success", false);
			failResult.put("message", "입차 기록을 찾을 수 없습니다");
			return failResult;
		}

		// 정산 서버용 JSON 헤더 설정
		HttpHeaders jsonHeaders = new HttpHeaders();
		jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

		// 정산 요청 바디 구성 (번호 + 나라 정보)
		Map<String, Object> paymentReqBody = new java.util.HashMap<>();
		paymentReqBody.put("parking_log_id", activeLog.getParkingLogId());
		paymentReqBody.put("vehicle_num", vehicleNum);
		paymentReqBody.put("license_plate_country", country);

		// 정산 요청 엔티티 조립
		HttpEntity<Map<String, Object>> paymentReq = new HttpEntity<>(paymentReqBody, jsonHeaders);

		try {
			// 8004 서버 호출
			ResponseEntity<Map> paymentRes = restTemplate.postForEntity(PYTHON_BRAIN_URL, paymentReq, Map.class);

			// 최종 데이터 취합 및 나라 정보 병합 후 반환
			Map<String, Object> finalResult = new java.util.HashMap<>(paymentRes.getBody());
			finalResult.put("license_plate_country", country);
			finalResult.put("is_success", true);
			return finalResult;

		} catch (org.springframework.web.client.HttpStatusCodeException e) {
			// 8004 서버가 404(기록 없음)일 때 실행
			Map<String, Object> failResult = new java.util.HashMap<>();

			// 데이터 구조 보존
			failResult.put("vehicle_num", vehicleNum);
			failResult.put("license_plate_country", country);
			failResult.put("is_success", false);
			failResult.put("message", "입차 기록을 찾을 수 없습니다");

			return failResult;
		}
	}

	// ==================================================================================================================

	// 주차 로그 출력
	public List<ParkingLogDto> selectAllParkingLogs() {
		return parkingLogDao.selectParkingLogs();
	}

	// 주차 로그 수정
	public void updateLogByAdmin(Long parkingLogId, String newNum, String newCountry, Integer adminId) {
		ParkingLogDto dto = new ParkingLogDto();
		dto.setParkingLogId(parkingLogId);
		dto.setVehicleNum(newNum);
		dto.setLicensePlateCountry(newCountry);
		dto.setAdminStaffId(adminId);
		parkingLogDao.updateParkingLog(dto);
	}

	// 주차 로그 삭제
	public void deleteParkingLog(Long parkingLogId, Integer adminId) {
		ParkingLogDto dto = new ParkingLogDto();
		dto.setParkingLogId(parkingLogId);
		dto.setAdminStaffId(adminId);
		parkingLogDao.deleteParkingLog(dto);
	}
}