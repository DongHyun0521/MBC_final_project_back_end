// mbcFinalProject1 - com.mbc.fin1.service - ParkingPredictionService.java
package com.mbc.fin1.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mbc.fin1.dao.ParkingPredictionDao;
import com.mbc.fin1.dto.ParkingPredictionDto;

@Service
public class ParkingPredictionService {

    @Autowired
    private ParkingPredictionDao predictionDao;
    
    @Autowired
    private HolidayService holidayService;
    
    @Value("${openapi.key}")
    private String API_KEY;

    // 파이썬 주차 수요 예측 서버 (8002) 주소
    @Value("${fastapi.parking-prediction.base-url}")
    private String parkingPredictionBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    
    private final Map<String, AtomicInteger> apiCallCounts = new ConcurrentHashMap<>() {{
        put("KMA_ULTRA", new AtomicInteger(0));		// 초단기
        put("KMA_VILAGE", new AtomicInteger(0));	// 단기
        put("KMA_MID", new AtomicInteger(0));		// 중기
        put("AIR_KOREA", new AtomicInteger(0));		// 대기질
    }};
    
    // ========== 실시간 API 캐시 저장소 ==========

    // 기상청 초단기 실시간 API 캐시 저장소
    private List<Map<String, Object>> cachedUltraItems = null;
    private String cachedUltraBaseDateTime = "";

    // 기상청 단기 실시간 API 캐시 저장소
    private List<Map<String, Object>> cachedVilageItems = null;
    private String cachedVilageBaseDateTime = "";

    // 기상청 중기 실시간 API 캐시 저장소
    private List<Map<String, Object>> cachedMidTaItems = null;		// 기온
    private List<Map<String, Object>> cachedMidLandItems = null;	// 육상 예보
    private String cachedMidTmFc = "";
    
    // 에어코리아 초단기 대기질 실시간 API 캐시 저장소
    private Map<String, Double> cachedAirKorea = new ConcurrentHashMap<>() {{
        put("pm10", 40.0);
        put("pm25", 15.0);
    }};
    private LocalDateTime airKoreaCacheTime = LocalDateTime.MIN;
    
    // 에어코리아 단기 대기질 실시간 API 캐시 저장소
    private Map<String, Map<String, Integer>> cachedAirKoreaForecastMap = new ConcurrentHashMap<>();
    private LocalDate cachedForecastTargetDate = null;
    private LocalDate cachedForecastCallDate = null;
    
    // 일 API 사용량 알려주는 함수
    @Scheduled(cron = "0 0 0 * * *")
    public void resetApiCounters() {
        System.out.println("======= [API Usage Daily Report] =======");
        apiCallCounts.forEach((name, count) -> {
            System.out.println(name + " : " + count.get() + " calls today.");
            count.set(0); // 초기화
        });
        System.out.println("========================================");
    }
    
    // ==========================================================
    // ⏰ [스케줄러] 대기질 API 백그라운드 갱신 로직 추가
    // ==========================================================
    
    // 1. 실시간 대기질: 매시 15분마다 실행 (하루 딱 24번 호출)
    @Scheduled(cron = "0 15 * * * *")
    public void fetchAirKoreaRealTimeBackground() {
        int current = apiCallCounts.get("AIR_KOREA").incrementAndGet();
        try {
            String url = String.format("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=%s&returnType=json&numOfRows=1&pageNo=1&stationName=종로구&dataTerm=DAILY&ver=1.0", API_KEY);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> respMap = (Map<String, Object>) response.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) respMap.get("body");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) bodyMap.get("items");

            if (itemList != null && !itemList.isEmpty()) {
                Map<String, Object> item = itemList.get(0);
                Map<String, Double> newCache = new ConcurrentHashMap<>();
                if (item.get("pm10Value") != null && !item.get("pm10Value").equals("-")) {
                    newCache.put("pm10", Double.parseDouble((String) item.get("pm10Value")));
                } else { newCache.put("pm10", 40.0); }
                
                if (item.get("pm25Value") != null && !item.get("pm25Value").equals("-")) {
                    newCache.put("pm25", Double.parseDouble((String) item.get("pm25Value")));
                } else { newCache.put("pm25", 15.0); }
                
                cachedAirKorea = newCache;
                airKoreaCacheTime = LocalDateTime.now();
                System.out.println("✅ [백그라운드] 실시간 대기질 갱신 완료! (오늘 누적: " + current + "회)");
            }
        } catch (Exception e) {
            System.err.println("⚠️ [백그라운드] 실시간 대기질 갱신 실패: " + e.getMessage());
        }
    }

    // 2. 예보 대기질: 하루 4번(05, 11, 17, 23시 10분)만 실행 (하루 딱 4번 호출)
    @Scheduled(cron = "0 10 5,11,17,23 * * *")
    public void fetchAirKoreaForecastBackground() {
        int current = apiCallCounts.get("AIR_KOREA").incrementAndGet();
        try {
            String url = String.format(
                "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth?serviceKey=%s&returnType=json&numOfRows=50&pageNo=1&searchDate=%s&ver=1.1",
                API_KEY, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> respMap = (Map<String, Object>) response.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) respMap.get("body");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) bodyMap.get("items");

            if (itemList != null) {
                Map<String, Map<String, Integer>> newForecastMap = new ConcurrentHashMap<>();
                for (Map<String, Object> item : itemList) {
                    String informData = (String) item.get("informData"); 
                    String code = (String) item.get("informCode");  
                    String gradeStr = (String) item.get("informGrade");  

                    if (informData == null || gradeStr == null) continue;

                    String seoulGrade = null;
                    for (String part : gradeStr.split(",")) {
                        if (part.contains("서울")) {
                            seoulGrade = part.split(":")[1].trim();
                            break;
                        }
                    }
                    if (seoulGrade == null) continue;

                    int grade = switch (seoulGrade) {
                        case "좋음"     -> 0;
                        case "보통"     -> 1;
                        case "나쁨"     -> 2;
                        case "매우나쁨" -> 3;
                        default         -> 1;
                    };

                    newForecastMap.putIfAbsent(informData, new HashMap<>());
                    if ("PM10".equals(code)) newForecastMap.get(informData).put("pm10Grade", grade);
                    if ("PM25".equals(code)) newForecastMap.get(informData).put("pm25Grade", grade);
                }
                cachedAirKoreaForecastMap = newForecastMap;
                cachedForecastCallDate = LocalDate.now();
                System.out.println("✅ [백그라운드] 예보 대기질 갱신 완료! (오늘 누적: " + current + "회)");
            }
        } catch (Exception e) {
            System.err.println("⚠️ [백그라운드] 예보 대기질 파싱 에러: " + e.getMessage());
        }
    }
    
    // ========== 실시간 API 연동 입차 대수 예측 ==========

    // 서버 켜질 때 및 정각/30분 마다 예측
    public void predictHalfHourlyParking() {
        System.out.println("=> ParkingPredictionService: predictHalfHourlyParking | " + new Date());

        LocalDateTime now = LocalDateTime.now();
        int snappedMinute = (now.getMinute() >= 30) ? 30 : 0;
        LocalDateTime baseTime = now.withMinute(snappedMinute).withSecond(0).withNano(0);

        // ── vshort: 매 30분마다 → 다음 6시간(12슬롯) 전체 재예측
        for (int i = 1; i <= 12; i++) {
            LocalDateTime targetTime = baseTime.plusMinutes(30L * i);
            predictAndSaveSingleTime(targetTime, false); // upsert → 덮어씀
        }

        // ── short: 매 3시간(정각)마다 → 6~72시간(132슬롯) 재예측
        if (now.getMinute() < 30 && now.getHour() % 3 == 0) {
            for (int i = 13; i <= 144; i++) {
                LocalDateTime targetTime = baseTime.plusMinutes(30L * i);
                predictAndSaveSingleTime(targetTime, false);
            }
        }

        // ── mid: 하루 2회(06시, 18시 정각)마다 → 3~10일 재예측
        if (now.getMinute() < 30 && (now.getHour() == 6 || now.getHour() == 18)) {
            LocalDate today = LocalDate.now();
            for (int d = 3; d <= 10; d++) {
                LocalDate targetDate = today.plusDays(d);
                for (int h = 0; h < 24; h++) {
                    for (int m = 0; m < 60; m += 30) {
                        LocalDateTime targetTime = LocalDateTime.of(targetDate, LocalTime.of(h, m));
                        predictAndSaveSingleTime(targetTime, true);
                    }
                }
            }
        }
    }

    // 초단기/단기: 입차 예측 데이터 반환
    public List<Map<String, Object>> getShortTermChartData(LocalDate startDate, LocalDate endDate) {
    	System.out.println("=> ParkingPredictionService: getShortTermChartData | "+ new Date());
    	
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            checkAndGenerateShortTermMissingData(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return predictionDao.getParkingChartData(startDate, endDate);
    }

    // 중기: 입차 예측 데이터 반환
    public List<Map<String, Object>> getMidTermChartData(LocalDate startDate, LocalDate endDate) {
    	System.out.println("=> ParkingPredictionService: getMidTermChartData | "+ new Date());
    	
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            checkAndGenerateMidTermMissingData(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return predictionDao.getMidTermChartData(startDate, endDate);
    }

    // 초단기/단기: parking_prediction에 해당 일 데이터가 00:00~23:30 중 하나라도 없을 시, 00:00~23:30 채우기
    private void checkAndGenerateShortTermMissingData(LocalDate targetDate) {
        LocalDateTime now = LocalDateTime.now();

        // 해당일의 기존 예측 타입 한 번에 조회 (datetime → forecastType 맵)
        Map<LocalDateTime, String> existingTypes = new HashMap<>();
        List<Map<String, Object>> rows = predictionDao.getForecastTypesByDate(targetDate);
        for (Map<String, Object> row : rows) {
            LocalDateTime dt = ((java.sql.Timestamp) row.get("targetDatetime")).toLocalDateTime();
            existingTypes.put(dt, (String) row.get("forecastType"));
        }

        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 30) {
                LocalDateTime targetTime = LocalDateTime.of(targetDate, LocalTime.of(h, m));
                long hoursDiff = ChronoUnit.HOURS.between(now, targetTime);
                String existing = existingTypes.get(targetTime);

                if (existing == null) {
                    // 슬롯 없음 → 새로 예측
                    predictAndSaveSingleTime(targetTime, false);
                } else {
                    // 모델 업그레이드 필요한지 확인
                    boolean needsUpgrade =
                        ("mid".equals(existing)   && hoursDiff <= 72) ||
                        ("short".equals(existing) && hoursDiff <= 6);
                    if (needsUpgrade) {
                        System.out.println("🔄 [" + targetTime + "] " + existing + " → 업그레이드 재예측");
                        predictAndSaveSingleTime(targetTime, false);
                    }
                }
            }
        }
    }

    // 중기: parking_prediction에 해당 일 데이터가 없을 시, 채우기
    private void checkAndGenerateMidTermMissingData(LocalDate targetDate) {
    	System.out.println("=> ParkingPredictionService: checkAndGenerateMidTermMissingData | "+ new Date());
    	
    	int count = predictionDao.countMidPredictionsByDate(targetDate);
        if (count < 48) { // 0에서 48로 변경!
            System.out.println("🚨 [" + targetDate + "] 중기 데이터 부족. 일일 평균을 위한 48연속 예측 발동!");
            for (int h = 0; h < 24; h++) {
                for (int m = 0; m < 60; m += 30) {
                    LocalDateTime targetTime = LocalDateTime.of(targetDate, LocalTime.of(h, m));
                    predictAndSaveSingleTime(targetTime, true); 
                }
            }
        }
    }

    // 초단기/단기/중기 모델 선택 및 예측 결과 DB 저장
    public void predictAndSaveSingleTime(LocalDateTime targetTime, boolean forceMid) {
    	System.out.println("=> ParkingPredictionService: predictAndSaveSingleTime | "+ new Date());
    	
    	// 타겟 날짜 및 시간까지 남은 시간 계산
        LocalDateTime now = LocalDateTime.now();
        long hoursDiff = ChronoUnit.HOURS.between(now, targetTime);
        
        LocalDate targetDate = targetTime.toLocalDate();
        LocalTime targetLocalTime = targetTime.toLocalTime();
        
        DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("M월 d일 E요일 H시 m분", Locale.KOREA);
        String prettyTime = targetTime.format(logFormatter);

        // 파이썬 server_parking_prediction.py로 보낼 파라미터 세팅
        Map<String, Object> aiRequest = new HashMap<>();
        DateTimeFormatter sendFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        aiRequest.put("target_datetime", targetTime.format(sendFormatter));
        aiRequest.put("month", targetDate.getMonthValue());
        aiRequest.put("dayofweek", targetDate.getDayOfWeek().getValue() - 1);
        aiRequest.put("hour", targetLocalTime.getHour());
        aiRequest.put("minute", targetLocalTime.getMinute());
        
        boolean isHoliday = holidayService.isHoliday(targetDate) || targetDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;
        aiRequest.put("is_holiday", isHoliday ? 1 : 0);

        // 기본값 세팅
        Double  temp       = 15.0;
        Double  rainfallMm = 0.0;
        Double  windSpeed  = null;
        Integer humidity   = null;
        Double  snowfallCm = 0.0;
        Double  pm10       = null;
        Double  pm25       = null;
        Integer pm10Grade  = null;
        Integer pm25Grade  = null;
        
        String forecastType;
        Map<String, Integer> resStats = new HashMap<>();

        // 중기 : 강제 중기 호출 / 72시간~240시간
        if (forceMid || hoursDiff > 72) {
            forecastType = "mid";
            
            // 일 단위 진료과별 예약 수 가져오기
            resStats = predictionDao.getTargetTimeReservationStats(targetDate, targetLocalTime);
            if(resStats == null) resStats = new HashMap<>();
            
            int daysAfter = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
            if (daysAfter < 3) daysAfter = 3;
            if (daysAfter > 10) daysAfter = 10;
            
            // 기상청 중기 예보 호출 및 매핑
            Map<String, String> midWeather = getKmaMidFcst(daysAfter);
            temp = Double.parseDouble(midWeather.getOrDefault("taAvg", "15.0"));
            
            String wfStr = midWeather.getOrDefault("wf", "맑음");
            if (wfStr.contains("비")) rainfallMm = 10.0;
            else if (wfStr.contains("눈")) { rainfallMm = 0.0; snowfallCm = 3.0; }
            else rainfallMm = 0.0;

            Map<String, Double> midAirQuality = getAirKoreaRealTime();
            double midPm10 = midAirQuality.getOrDefault("pm10", 40.0);
            double midPm25 = midAirQuality.getOrDefault("pm25", 15.0);
            pm10Grade = midPm10 > 80 ? 3 : (midPm10 > 30 ? 2 : 1);
            pm25Grade = midPm25 > 35 ? 3 : (midPm25 > 15 ? 2 : 1);
        }
        
        // 초단기: 현재~6시간
        else if (hoursDiff <= 6) {
            forecastType = "vshort";
            
            // 30분 단위 진료과별 예약 수 가져오기
            resStats = predictionDao.getTargetTimeReservationStats(targetDate, targetLocalTime);
            if(resStats == null) resStats = new HashMap<>(); 

            // 기상청 초단기 예보 호출 및 매핑
            Map<String, String> ultraSrtWeather = getKmaUltraSrtFcst(targetDate, targetLocalTime);
            temp = Double.parseDouble(ultraSrtWeather.getOrDefault("T1H", "15.0"));
            rainfallMm = parseRainSnowString(ultraSrtWeather.getOrDefault("RN1", "0.0"));
            windSpeed = Double.parseDouble(ultraSrtWeather.getOrDefault("WSD", "2.0"));
            humidity = (int) Double.parseDouble(ultraSrtWeather.getOrDefault("REH", "50"));
            
            // 에어코리아 대기질 호출 및 매핑
            Map<String, Double> airQuality = getAirKoreaRealTime();
            pm10 = airQuality.getOrDefault("pm10", 40.0);
            pm25 = airQuality.getOrDefault("pm25", 15.0);
        }
        
        // 단기: 6시간~72시간
        else {
            forecastType = "short";
            
            // 30분 단위 진료과별 예약 수 가져오기
            resStats = predictionDao.getTargetTimeReservationStats(targetDate, targetLocalTime);
            if(resStats == null) resStats = new HashMap<>(); 
            
            // 기상청 단기 예보 호출 및 매핑
            Map<String, String> vilageWeather = getKmaVilageFcst(targetDate, targetLocalTime);
            temp = Double.parseDouble(vilageWeather.getOrDefault("TMP", "15.0"));
            rainfallMm = parseRainSnowString(vilageWeather.getOrDefault("PCP", "0.0"));
            windSpeed = Double.parseDouble(vilageWeather.getOrDefault("WSD", "2.0"));
            humidity = (int) Double.parseDouble(vilageWeather.getOrDefault("REH", "50"));
            snowfallCm = parseRainSnowString(vilageWeather.getOrDefault("SNO", "0.0"));
            
            // 에어코리아 대기질 호출 및 매핑
            Map<String, Integer> forecastGrade = getAirKoreaForecastGrade(targetDate);  // 예보 API
            pm10Grade = forecastGrade.getOrDefault("pm10Grade", 1);
            pm25Grade = forecastGrade.getOrDefault("pm25Grade", 1);

        }
        
        // 파라미터 취합 및 파이썬 전송
        aiRequest.put("forecast_type", forecastType);
        aiRequest.put("temp", temp);
        aiRequest.put("rainfall_mm", rainfallMm);
        aiRequest.put("wind_speed",  windSpeed  != null ? windSpeed  : 2.0);
        aiRequest.put("humidity",    humidity   != null ? humidity   : 50);
        aiRequest.put("pm10",        pm10       != null ? pm10       : 40.0);
        aiRequest.put("pm25",        pm25       != null ? pm25       : 15.0);
        aiRequest.put("pm10_grade",  pm10Grade  != null ? pm10Grade  : 1);
        aiRequest.put("pm25_grade",  pm25Grade  != null ? pm25Grade  : 1);

        int resInternal = ((Number) resStats.getOrDefault("resInternal", 0)).intValue();
        int resOrthopedics = ((Number) resStats.getOrDefault("resOrthopedics", 0)).intValue();
        int resNeurosurgery = ((Number) resStats.getOrDefault("resNeurosurgery", 0)).intValue();
        int resPediatrics = ((Number) resStats.getOrDefault("resPediatrics", 0)).intValue();
        int resEnt = ((Number) resStats.getOrDefault("resEnt", 0)).intValue();
        int resDermatology = ((Number) resStats.getOrDefault("resDermatology", 0)).intValue();
        int resOphthalmology = ((Number) resStats.getOrDefault("resOphthalmology", 0)).intValue();
        int resDentistry = ((Number) resStats.getOrDefault("resDentistry", 0)).intValue();
        int resPsychiatry = ((Number) resStats.getOrDefault("resPsychiatry", 0)).intValue();
        int resTotal = ((Number) resStats.getOrDefault("resTotal", 0)).intValue();

        aiRequest.put("예약_내과", resInternal);
        aiRequest.put("예약_정형외과", resOrthopedics);
        aiRequest.put("예약_신경외과", resNeurosurgery);
        aiRequest.put("예약_소아청소년과", resPediatrics);
        aiRequest.put("예약_이비인후과", resEnt);
        aiRequest.put("예약_피부과", resDermatology);
        aiRequest.put("예약_안과", resOphthalmology);
        aiRequest.put("예약_치과", resDentistry);
        aiRequest.put("예약_정신건강의학과", resPsychiatry);

        // 파이썬 server_parking_prediction.py 호출
        int predictedCars = callPythonPredictionServer(aiRequest);

        ParkingPredictionDto prediction = ParkingPredictionDto.builder()
                .targetDatetime(targetTime)
                .predictedCars(predictedCars)
                .forecastType(forecastType)
                .temp(temp)
                .rainfallMm(rainfallMm)
                .windSpeed(windSpeed)
                .humidity(humidity)
                .snowfallCm(snowfallCm)
                .pm10(pm10)
                .pm25(pm25)
                .pm10Grade(pm10Grade)
                .pm25Grade(pm25Grade)
                .isHoliday(isHoliday)
                .resInternal(resInternal)
                .resOrthopedics(resOrthopedics)
                .resNeurosurgery(resNeurosurgery)
                .resPediatrics(resPediatrics)
                .resEnt(resEnt)
                .resDermatology(resDermatology)
                .resOphthalmology(resOphthalmology)
                .resDentistry(resDentistry)
                .resPsychiatry(resPsychiatry)
                .resTotal(resTotal)
                .build();

        // DB 저장
        predictionDao.upsertPrediction(prediction);
        
        System.out.println("=> [" + prettyTime + "] "+ forecastType +" 모델 예측 저장 완료: " + predictedCars + "대 | " + new Date());
    }

    // 파이썬 server_parking_prediction.py 부르기 
    private int callPythonPredictionServer(Map<String, Object> requestData) {
    	System.out.println("=> ParkingPredictionService: callPythonPredictionServer | " + new Date());
    	
        String aiServerUrl = parkingPredictionBaseUrl + "/parking_prediction";
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(aiServerUrl, requestData, Map.class);
            Map<String, Object> result = response.getBody();
            if (result != null && result.containsKey("predicted_cars")) {
                return ((Number) result.get("predicted_cars")).intValue();
            }
        } catch (Exception e) {
            System.err.println("🚨 파이썬 주차 예측 서버 응답 없음! " + e.getMessage());
        }
        return 0; 
    }
    
    // ========== 실시간 API ==========
    
    // 실시간 API의 문자열에서 숫자만 추출
    private double parseRainSnowString(String value) {
    	System.out.println("=> ParkingPredictionService: parseRainSnowString | " + new Date());
    	
        if (value == null || value.contains("없음") || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        } catch (Exception e) { return 0.0; }
    }

    // 기상청 초단기 (getUltraSrtFcst) (종로구): 현재~6시간 (45분 업데이트)
    @SuppressWarnings("unchecked")
    private Map<String, String> getKmaUltraSrtFcst(LocalDate targetDate, LocalTime targetTime) {
    	System.out.println("=> ParkingPredictionService: getKmaUltraSrtFcst | "+ new Date());
    	
        Map<String, String> result = new HashMap<>();
        try {
        	// 현재시간 기준 최근 발표 날짜 및 시간 계산하기
            LocalDateTime calcTime = LocalDateTime.now().minusMinutes(45);
            String baseDate = calcTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = String.format("%02d30", calcTime.getHour()); 
            String currentBase = baseDate + baseTime;

            // 발표 날짜 및 시간이 다르다면, API 호출 (같으면 if문 건너뛰기)
            if (cachedUltraItems == null || !currentBase.equals(cachedUltraBaseDateTime)) {
                // 🔥 실제 API 통신이 일어날 때만 카운터 증가!
                int current = apiCallCounts.get("KMA_ULTRA").incrementAndGet();
                System.out.println("🌐 [초단기] 실제 API 통신 발생 (" + current + " / 10,000)");
                
            	System.out.println("=> 기상청 초단기 (getUltraSrtFcst) 호출 시작 | " + new Date());
            	
                String url = String.format("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey=%s&pageNo=1&numOfRows=100&dataType=JSON&base_date=%s&base_time=%s&nx=60&ny=127", API_KEY, baseDate, baseTime);
                
                // JOSN 파싱 작업
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                Map<String, Object> respMap = (Map<String, Object>) response.get("response");
                Map<String, Object> bodyMap = (Map<String, Object>) respMap.get("body");
                Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");
                
                // 파싱한 JSON 데이터를 리스트맵에 저장
                cachedUltraItems = (List<Map<String, Object>>) itemsMap.get("item");
                cachedUltraBaseDateTime = currentBase;
                
                System.out.println("=> 기상청 초단기 (getUltraSrtFcst) 호출 종료 | " + new Date());
            }
            
            // 타겟 날짜 및 시간
            String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String targetTimeStr = String.format("%02d00", targetTime.getHour());

            // 타겟 날짜 및 시간의 데이터 가져오기
            for (Map<String, Object> item : cachedUltraItems) {
                if (targetDateStr.equals(item.get("fcstDate")) && targetTimeStr.equals(item.get("fcstTime"))) {
                    result.put((String)item.get("category"), (String)item.get("fcstValue"));
                }
            }
        } catch (Exception e) {
            System.err.println("!! 기상청 초단기 (getUltraSrtFcst) 호출 에러: " + e.getMessage());
        }
        return result;
    }

    // 기상청 단기 (getVilageFcst) (종로구): 6시간~3일 (02, 05, 08, 11, 14, 17, 20, 13시 업데이트)
    @SuppressWarnings("unchecked")
    private Map<String, String> getKmaVilageFcst(LocalDate targetDate, LocalTime targetTime) {
    	System.out.println("=> ParkingPredictionService: getKmaVilageFcst | "+ new Date());
    	
        Map<String, String> result = new HashMap<>();
        try {
        	// 현재시간 기준 최근 발표 날짜 및 시간 계산하기
            LocalDateTime calcTime = LocalDateTime.now().minusMinutes(15);
            int currentHour = calcTime.getHour();
            int baseHour = 23; 
            LocalDate baseDateDt = calcTime.toLocalDate();
            
            int[] baseHours = {2, 5, 8, 11, 14, 17, 20, 23};
            boolean found = false;
            for (int i = baseHours.length - 1; i >= 0; i--) {
                if (currentHour >= baseHours[i]) { baseHour = baseHours[i]; found = true; break; }
            }
            if (!found) baseDateDt = baseDateDt.minusDays(1);

            String baseDate = baseDateDt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = String.format("%02d00", baseHour);
            String currentBase = baseDate + baseTime;

            // 발표 날짜 및 시간이 다르다면, API 호출 (같으면 if문 건너뛰기)
            if (cachedVilageItems == null || !currentBase.equals(cachedVilageBaseDateTime)) {
                // 🔥 실제 API 통신이 일어날 때만 카운터 증가!
                int current = apiCallCounts.get("KMA_VILAGE").incrementAndGet();
                System.out.println("🌐 [단기] 실제 API 통신 발생 (" + current + " / 10,000)");

            	System.out.println("=> 기상청 단기 (getVilageFcst) 호출 시작 | " + new Date());
                String url = String.format("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=%s&pageNo=1&numOfRows=500&dataType=JSON&base_date=%s&base_time=%s&nx=60&ny=127", API_KEY, baseDate, baseTime);
                
                // JOSN 파싱 작업
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                Map<String, Object> respMap = (Map<String, Object>) response.get("response");
                Map<String, Object> bodyMap = (Map<String, Object>) respMap.get("body");
                Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");
                
                // 파싱한 JSON 데이터를 리스트맵에 저장
                cachedVilageItems = (List<Map<String, Object>>) itemsMap.get("item");
                cachedVilageBaseDateTime = currentBase;
                
                System.out.println("=> 기상청 단기 (getVilageFcst) 호출 종료 | " + new Date());
            }
            
            // 타겟 날짜 및 시간
            String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String targetTimeStr = String.format("%02d00", targetTime.getHour()); 

            // 타겟 날짜 및 시간의 데이터 가져오기
            for (Map<String, Object> item : cachedVilageItems) {
                if (targetDateStr.equals(item.get("fcstDate")) && targetTimeStr.equals(item.get("fcstTime"))) {
                    result.put((String)item.get("category"), (String)item.get("fcstValue"));
                }
            }
        } catch (Exception e) {
            System.err.println("!! 기상청 단기 (getVilageFcst) 호출 에러: " + e.getMessage());
        }
        return result;
    }

    // 기상청 중기 (종로구): 3일~10일 (06, 18시 업데이트)
    @SuppressWarnings("unchecked")
    private Map<String, String> getKmaMidFcst(int daysAfter) {
    	System.out.println("=> ParkingPredictionService: getKmaMidFcst | "+ new Date());
    	
        Map<String, String> result = new HashMap<>();
        try {
        	// 현재시간 기준 최근 발표 날짜 및 시간 계산하기
            LocalDateTime now = LocalDateTime.now();
            String tmFc;
            if (now.getHour() < 6) tmFc = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd1800"));
            else if (now.getHour() < 18) tmFc = now.format(DateTimeFormatter.ofPattern("yyyyMMdd0600"));
            else tmFc = now.format(DateTimeFormatter.ofPattern("yyyyMMdd1800"));

            // 발표 날짜 및 시간이 다르다면, API 호출 (같으면 if문 건너뛰기)
            if (cachedMidTaItems == null || cachedMidLandItems == null || !tmFc.equals(cachedMidTmFc)) {
                // 🔥 실제 API 통신이 일어날 때만 카운터 증가!
                int current = apiCallCounts.get("KMA_MID").incrementAndGet();
                System.out.println("🌐 [중기] 실제 API 통신 발생 (" + current + " / 10,000)");

            	System.out.println("=> 기상청 중기 호출 시작 | " + new Date());
                
                System.out.println("=> 기상청 중기 (기온) 호출 시작 | " + new Date());
                String taUrl = String.format("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa?serviceKey=%s&pageNo=1&numOfRows=10&dataType=JSON&regId=11B10101&tmFc=%s", API_KEY, tmFc);
                Map<String, Object> taRes = restTemplate.getForObject(taUrl, Map.class);
                cachedMidTaItems = (List<Map<String, Object>>) ((Map)((Map)((Map)taRes.get("response")).get("body")).get("items")).get("item");
                System.out.println("=> 기상청 중기 (기온) 호출 종료 | " + new Date());
                
                System.out.println("=> 기상청 중기 (날씨) 호출 시작 | " + new Date());
                String landUrl = String.format("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst?serviceKey=%s&pageNo=1&numOfRows=10&dataType=JSON&regId=11B00000&tmFc=%s", API_KEY, tmFc);
                Map<String, Object> landRes = restTemplate.getForObject(landUrl, Map.class);
                cachedMidLandItems = (List<Map<String, Object>>) ((Map)((Map)((Map)landRes.get("response")).get("body")).get("items")).get("item");
                System.out.println("=> 기상청 중기 (날씨) 호출 종료 | " + new Date());
                
                cachedMidTmFc = tmFc;
                System.out.println("=> 기상청 중기 호출 종료 | " + new Date());
            }
            
            // 타겟 날짜의 기온 가져오기
            if (!cachedMidTaItems.isEmpty()) {
                Map<String, Object> item = cachedMidTaItems.get(0);
                // 대체값 집어넣기
                double taMin = Double.parseDouble(String.valueOf(item.getOrDefault("taMin" + daysAfter, "9.9")));
                double taMax = Double.parseDouble(String.valueOf(item.getOrDefault("taMax" + daysAfter, "19.7")));
                // 타겟 날짜의 평균 기온 구하기
                result.put("taAvg", String.valueOf((taMin + taMax) / 2.0));
            }

            // 타겟 날짜의 날씨 가져오기
            if (!cachedMidLandItems.isEmpty()) {
                Map<String, Object> item = cachedMidLandItems.get(0);
                // 3~7일: 오전/오후 | 8~10일: 하루
                String wfKey = daysAfter <= 7 ? "wf" + daysAfter + "Am" : "wf" + daysAfter;
                result.put("wf", String.valueOf(item.getOrDefault(wfKey, "맑음")));
            }
        } catch (Exception e) {
            System.err.println("!! 기상청 중기 호출 에러: " + e.getMessage());
        }
        return result;
    }

    // 대기질 초단기 (종로구): 현재
    @SuppressWarnings("unchecked")
    private Map<String, Double> getAirKoreaRealTime() {
    	System.out.println("=> ParkingPredictionService: getAirKoreaRealTime | "+ new Date());
    	
        // API 호출 부분 삭제 (스케줄러가 채워둔 캐시 반환)
        if (cachedAirKorea != null) {
            return cachedAirKorea;
        }
        
        Map<String, Double> defaultMap = new HashMap<>();
        defaultMap.put("pm10", 40.0);
        defaultMap.put("pm25", 15.0);
        return defaultMap;
    }
    
    // 대기질 단기 (서울): 오늘, 내일, 모레
    @SuppressWarnings("unchecked")
    private Map<String, Integer> getAirKoreaForecastGrade(LocalDate targetDate) {
        String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 스케줄러가 채워둔 캐시 반환
        Map<String, Integer> result = cachedAirKoreaForecastMap.get(targetDateStr);
        
        if (result == null) {
            result = new HashMap<>();
            result.put("pm10Grade", 1);
            result.put("pm25Grade", 1);
        }
        
        // 30분 이내 재호출 시, API 미호출 (429 Error: Too Many Requests 방지 목적)
        // API 호출 성공 시 result에 값이 있고, 실패 시 비어있음
        // 실패했을 때 이전 캐시가 있으면 캐시를 대신 반환, 없으면 빈 map 반환
        // ↓ 추가: 예측하려는 날짜의 예보만 사용
        // 서울 등급만 파싱
        return result;
    }
    
 // 현재 날씨 조회 (대시보드용): 기상청 초단기 + 에어코리아 실시간
    public Map<String, Object> getCurrentWeather() {
        System.out.println("=> ParkingPredictionService: getCurrentWeather | " + new Date());

        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();

        Map<String, Object> result = new HashMap<>();

        // 기상청 초단기 예보 (현재 시각 기준)
        Map<String, String> ultra = getKmaUltraSrtFcst(today, now);

        double temp       = Double.parseDouble(ultra.getOrDefault("T1H", "15.0"));
        double rainfallMm = parseRainSnowString(ultra.getOrDefault("RN1", "0.0"));
        double windSpeed  = Double.parseDouble(ultra.getOrDefault("WSD", "2.0"));
        int    humidity   = (int) Double.parseDouble(ultra.getOrDefault("REH", "50"));
        double snowfallCm = 0.0;

        // SKY(하늘 상태) + PTY(강수 형태) → 날씨 문자열
        int pty = 0, sky = 1;
        try { pty = Integer.parseInt(ultra.getOrDefault("PTY", "0")); } catch (Exception ignored) {}
        try { sky = Integer.parseInt(ultra.getOrDefault("SKY", "1")); } catch (Exception ignored) {}

        String skyCondition;
        if      (pty == 1 || pty == 5) { skyCondition = "비";       }
        else if (pty == 2 || pty == 6) { skyCondition = "비/눈";    }
        else if (pty == 3 || pty == 7) { skyCondition = "눈"; snowfallCm = rainfallMm; rainfallMm = 0.0; }
        else if (sky == 1)             { skyCondition = "맑음";     }
        else if (sky == 3)             { skyCondition = "구름많음"; }
        else                           { skyCondition = "흐림";     }

        // 에어코리아 실시간 대기질
        Map<String, Double> air = getAirKoreaRealTime();
        double pm10 = air.getOrDefault("pm10", 40.0);
        double pm25 = air.getOrDefault("pm25", 15.0);

        result.put("skyCondition", skyCondition);
        result.put("temp",         temp);
        result.put("rainfallMm",   rainfallMm);
        result.put("windSpeed",    windSpeed);
        result.put("humidity",     humidity);
        result.put("snowfallCm",   snowfallCm);
        result.put("pm10",         pm10);
        result.put("pm25",         pm25);

        return result;
    }
}