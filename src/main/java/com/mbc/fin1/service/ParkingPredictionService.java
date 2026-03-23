package com.mbc.fin1.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    // =========================================================================
    // 💾 [메모리 캐시 수첩] API 과부하(429 에러) 방지용 저장소
    // =========================================================================
    private Map<String, Double> cachedAirKorea = null;
    private LocalDateTime airKoreaCacheTime = LocalDateTime.MIN;

    private List<Map<String, Object>> cachedUltraItems = null;
    private String cachedUltraBaseDateTime = "";

    private List<Map<String, Object>> cachedVilageItems = null;
    private String cachedVilageBaseDateTime = "";

    private List<Map<String, Object>> cachedMidTaItems = null;
    private List<Map<String, Object>> cachedMidLandItems = null;
    private String cachedMidTmFc = "";

    // =========================================================================
    // ⏰ 1. 스케줄러용: 현재 시간 기준으로 30분 뒤 실시간 예측
    // =========================================================================
    public void predictHalfHourlyParking() {
        LocalDateTime now = LocalDateTime.now();
        int currentMinute = now.getMinute();
        int snappedMinute = (currentMinute >= 30) ? 30 : 0; 
        LocalDateTime targetTime = now.withMinute(snappedMinute).withSecond(0).withNano(0).plusMinutes(30);
        
        predictAndSaveSingleTime(targetTime, false);
    }

    // =========================================================================
    // 📊 2. 프론트엔드 [왼쪽 차트] 용: 단기(Short) 데이터 반환 (30분 단위)
    // =========================================================================
    public List<Map<String, Object>> getShortTermChartData(LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            checkAndGenerateShortTermMissingData(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return predictionDao.getParkingChartData(startDate, endDate);
    }

    // =========================================================================
    // 📊 3. 프론트엔드 [오른쪽 차트] 용: 중기(Mid) 데이터 반환 (일 단위)
    // =========================================================================
    public List<Map<String, Object>> getMidTermChartData(LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            checkAndGenerateMidTermMissingData(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return predictionDao.getMidTermChartData(startDate, endDate);
    }

    // =========================================================================
    // 🕵️‍♂️ 4-1. 빈칸 채우기 로직 (초단기/단기용 - 하루 48칸)
    // =========================================================================
    private void checkAndGenerateShortTermMissingData(LocalDate targetDate) {
        int count = predictionDao.countPredictionsByDate(targetDate);
        if (count < 48) {
            System.out.println("🚨 [" + targetDate + "] 단기 데이터 부족. 48연속 예측 발동!");
            for (int h = 0; h < 24; h++) {
                for (int m = 0; m < 60; m += 30) {
                    LocalDateTime targetTime = LocalDateTime.of(targetDate, LocalTime.of(h, m));
                    predictAndSaveSingleTime(targetTime, false);
                }
            }
        }
    }

    // 🕵️‍♂️ 4-2. 빈칸 채우기 로직 (중기용 - 하루 1칸 피크 예측)
    private void checkAndGenerateMidTermMissingData(LocalDate targetDate) {
        int count = predictionDao.countPredictionsByDate(targetDate);
        if (count == 0) {
            System.out.println("🚨 [" + targetDate + "] 중기 데이터 부족. 일일 통합 예측 발동!");
            LocalDateTime targetTime = LocalDateTime.of(targetDate, LocalTime.of(14, 0));
            predictAndSaveSingleTime(targetTime, true); 
        }
    }

    // =========================================================================
    // 🤖 5. 만능 지휘관: 타겟 시간의 거리에 따라 3가지 API / 모델 라우팅
    // =========================================================================
    public void predictAndSaveSingleTime(LocalDateTime targetTime, boolean forceMid) {
        LocalDateTime now = LocalDateTime.now();
        long hoursDiff = ChronoUnit.HOURS.between(now, targetTime);
        
        LocalDate targetDate = targetTime.toLocalDate();
        LocalTime targetLocalTime = targetTime.toLocalTime();
        
        DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("M월 d일 E요일 H시 m분", Locale.KOREA);
        String prettyTime = targetTime.format(logFormatter);

        Map<String, Object> aiRequest = new HashMap<>();
        aiRequest.put("month", targetDate.getMonthValue());
        aiRequest.put("dayofweek", targetDate.getDayOfWeek().getValue() - 1);
        aiRequest.put("hour", targetLocalTime.getHour());
        aiRequest.put("minute", targetLocalTime.getMinute());
        
        boolean isHoliday = holidayService.isHoliday(targetDate);
        aiRequest.put("is_holiday", isHoliday ? 1 : 0);

        // 기본값 세팅
        double temp = 15.0;        
        double rainfallMm = 0.0;   
        double windSpeed = 2.0;
        int humidity = 50;
        double snowfallCm = 0.0;
        double pm10 = 40.0; double pm25 = 15.0;
        int pm10Grade = 1; int pm25Grade = 1;
        
        String forecastType;
        Map<String, Integer> resStats = new HashMap<>();

        // ------------------------------------------------------------------
        // 🔴 [중기 모델 (mid)] : 강제 중기 호출이거나 72시간 초과일 때
        // ------------------------------------------------------------------
        if (forceMid || hoursDiff > 72) {
            forecastType = "mid";
            resStats = predictionDao.getDailyReservationStats(targetDate);
            if(resStats == null) resStats = new HashMap<>(); 
            
            int daysAfter = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
            if (daysAfter < 3) daysAfter = 3;
            if (daysAfter > 10) daysAfter = 10;
            
            Map<String, String> midWeather = getKmaMidFcst(daysAfter);
            temp = Double.parseDouble(midWeather.getOrDefault("taAvg", "15.0"));
            
            String wfStr = midWeather.getOrDefault("wf", "맑음");
            if(wfStr.contains("비")) rainfallMm = 10.0;
            else if(wfStr.contains("눈")) rainfallMm = 5.0;
            else rainfallMm = 0.0;
        }
        // ------------------------------------------------------------------
        // 🟢 [초단기 모델 (vshort)] : 0 ~ 6시간 이내
        // ------------------------------------------------------------------
        else if (hoursDiff <= 6) {
            forecastType = "vshort";
            resStats = predictionDao.getTargetTimeReservationStats(targetDate, targetLocalTime);
            if(resStats == null) resStats = new HashMap<>(); 

            Map<String, String> ultraSrtWeather = getKmaUltraSrtFcst(targetDate, targetLocalTime);
            temp = Double.parseDouble(ultraSrtWeather.getOrDefault("T1H", "15.0"));
            rainfallMm = parseRainSnowString(ultraSrtWeather.getOrDefault("RN1", "0.0"));
            windSpeed = Double.parseDouble(ultraSrtWeather.getOrDefault("WSD", "2.0"));
            humidity = (int) Double.parseDouble(ultraSrtWeather.getOrDefault("REH", "50"));
            
            Map<String, Double> airQuality = getAirKoreaRealTime();
            pm10 = airQuality.getOrDefault("pm10", 40.0);
            pm25 = airQuality.getOrDefault("pm25", 15.0);
        }
        // ------------------------------------------------------------------
        // 🟡 [단기 모델 (short)] : 6시간 초과 ~ 3일(72시간) 이내
        // ------------------------------------------------------------------
        else {
            forecastType = "short";
            resStats = predictionDao.getTargetTimeReservationStats(targetDate, targetLocalTime);
            if(resStats == null) resStats = new HashMap<>(); 
            
            Map<String, String> vilageWeather = getKmaVilageFcst(targetDate, targetLocalTime);
            temp = Double.parseDouble(vilageWeather.getOrDefault("TMP", "15.0"));
            rainfallMm = parseRainSnowString(vilageWeather.getOrDefault("PCP", "0.0"));
            windSpeed = Double.parseDouble(vilageWeather.getOrDefault("WSD", "2.0"));
            humidity = (int) Double.parseDouble(vilageWeather.getOrDefault("REH", "50"));
            snowfallCm = parseRainSnowString(vilageWeather.getOrDefault("SNO", "0.0"));
            
            Map<String, Double> airQuality = getAirKoreaRealTime(); 
            double currentPm10 = airQuality.getOrDefault("pm10", 40.0);
            double currentPm25 = airQuality.getOrDefault("pm25", 15.0);
            pm10Grade = currentPm10 > 80 ? 3 : (currentPm10 > 30 ? 2 : 1);
            pm25Grade = currentPm25 > 35 ? 3 : (currentPm25 > 15 ? 2 : 1);
        }

        // ------------------------------------------------------------------
        // [공통] 변수 취합 및 파이썬 전송
        // ------------------------------------------------------------------
        aiRequest.put("forecast_type", forecastType);
        aiRequest.put("temp", temp);
        aiRequest.put("rainfall_mm", rainfallMm);
        aiRequest.put("wind_speed", windSpeed);
        aiRequest.put("humidity", humidity);
        aiRequest.put("snowfall_cm", snowfallCm);
        aiRequest.put("pm10", pm10); 
        aiRequest.put("pm25", pm25); 
        aiRequest.put("pm10_grade", pm10Grade); 
        aiRequest.put("pm25_grade", pm25Grade); 

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

        predictionDao.upsertPrediction(prediction);
        System.out.println("✅ [" + prettyTime + "] AI("+ forecastType +") 예측 저장 완료: [" + predictedCars + "대]");
    }

    private int callPythonPredictionServer(Map<String, Object> requestData) {
        RestTemplate restTemplate = new RestTemplate();
        String aiServerUrl = "http://localhost:8002/parking_prediction"; 
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

    // =========================================================================
    // 🌐 [API 연동 모듈] 기상청 & 에어코리아 파싱 로직 (캐싱 적용 완벽판)
    // =========================================================================
    
    private double parseRainSnowString(String value) {
        if (value == null || value.contains("없음") || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        } catch (Exception e) { return 0.0; }
    }

    // 1. 기상청 초단기 예보 (getUltraSrtFcst)
    @SuppressWarnings("unchecked")
    private Map<String, String> getKmaUltraSrtFcst(LocalDate targetDate, LocalTime targetTime) {
        Map<String, String> result = new HashMap<>();
        try {
            LocalDateTime calcTime = LocalDateTime.now().minusMinutes(45);
            String baseDate = calcTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = String.format("%02d30", calcTime.getHour()); 
            String currentBase = baseDate + baseTime;

            // 💡 [캐시 로직] 동일한 baseTime이면 다시 호출하지 않고 저장된 리스트 재사용!
            if (cachedUltraItems == null || !currentBase.equals(cachedUltraBaseDateTime)) {
                String url = String.format("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey=%s&pageNo=1&numOfRows=100&dataType=JSON&base_date=%s&base_time=%s&nx=60&ny=127", API_KEY, baseDate, baseTime);
                RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                Map<String, Object> respMap = (Map<String, Object>) response.get("response");
                Map<String, Object> bodyMap = (Map<String, Object>) respMap.get("body");
                Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");
                cachedUltraItems = (List<Map<String, Object>>) itemsMap.get("item");
                cachedUltraBaseDateTime = currentBase;
                System.out.println("🔄 [캐시 갱신] 기상청 초단기예보 API 호출 완료");
            }

            String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String targetTimeStr = String.format("%02d00", targetTime.getHour());

            for (Map<String, Object> item : cachedUltraItems) {
                if (targetDateStr.equals(item.get("fcstDate")) && targetTimeStr.equals(item.get("fcstTime"))) {
                    result.put((String)item.get("category"), (String)item.get("fcstValue"));
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ 기상청 초단기 파싱 에러: " + e.getMessage());
        }
        return result;
    }

    // 2. 기상청 단기 예보 (getVilageFcst)
    @SuppressWarnings("unchecked")
    private Map<String, String> getKmaVilageFcst(LocalDate targetDate, LocalTime targetTime) {
        Map<String, String> result = new HashMap<>();
        try {
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

            // 💡 [캐시 로직] 동일한 단기예보 타임이면 재사용!
            if (cachedVilageItems == null || !currentBase.equals(cachedVilageBaseDateTime)) {
                String url = String.format("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=%s&pageNo=1&numOfRows=500&dataType=JSON&base_date=%s&base_time=%s&nx=60&ny=127", API_KEY, baseDate, baseTime);
                RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                Map<String, Object> respMap = (Map<String, Object>) response.get("response");
                Map<String, Object> bodyMap = (Map<String, Object>) respMap.get("body");
                Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");
                cachedVilageItems = (List<Map<String, Object>>) itemsMap.get("item");
                cachedVilageBaseDateTime = currentBase;
                System.out.println("🔄 [캐시 갱신] 기상청 단기예보 API 호출 완료");
            }

            String targetDateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String targetTimeStr = String.format("%02d00", targetTime.getHour()); 

            for (Map<String, Object> item : cachedVilageItems) {
                if (targetDateStr.equals(item.get("fcstDate")) && targetTimeStr.equals(item.get("fcstTime"))) {
                    result.put((String)item.get("category"), (String)item.get("fcstValue"));
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ 기상청 단기 파싱 에러: " + e.getMessage());
        }
        return result;
    }

    // 3. 기상청 중기 예보 (기온 + 육상 날씨)
    @SuppressWarnings("unchecked")
    private Map<String, String> getKmaMidFcst(int daysAfter) {
        Map<String, String> result = new HashMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            String tmFc;
            if (now.getHour() < 6) tmFc = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd1800"));
            else if (now.getHour() < 18) tmFc = now.format(DateTimeFormatter.ofPattern("yyyyMMdd0600"));
            else tmFc = now.format(DateTimeFormatter.ofPattern("yyyyMMdd1800"));

            // 💡 [캐시 로직] 중기예보도 갱신시간 같으면 재사용!
            if (cachedMidTaItems == null || cachedMidLandItems == null || !tmFc.equals(cachedMidTmFc)) {
                RestTemplate restTemplate = new RestTemplate();
                
                String taUrl = String.format("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa?serviceKey=%s&pageNo=1&numOfRows=10&dataType=JSON&regId=11B10101&tmFc=%s", API_KEY, tmFc);
                Map<String, Object> taRes = restTemplate.getForObject(taUrl, Map.class);
                cachedMidTaItems = (List<Map<String, Object>>) ((Map)((Map)((Map)taRes.get("response")).get("body")).get("items")).get("item");

                String landUrl = String.format("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst?serviceKey=%s&pageNo=1&numOfRows=10&dataType=JSON&regId=11B00000&tmFc=%s", API_KEY, tmFc);
                Map<String, Object> landRes = restTemplate.getForObject(landUrl, Map.class);
                cachedMidLandItems = (List<Map<String, Object>>) ((Map)((Map)((Map)landRes.get("response")).get("body")).get("items")).get("item");
                
                cachedMidTmFc = tmFc;
                System.out.println("🔄 [캐시 갱신] 기상청 중기예보 API 호출 완료");
            }
            
            if (!cachedMidTaItems.isEmpty()) {
                Map<String, Object> item = cachedMidTaItems.get(0);
                double taMin = Double.parseDouble(String.valueOf(item.getOrDefault("taMin" + daysAfter, "10")));
                double taMax = Double.parseDouble(String.valueOf(item.getOrDefault("taMax" + daysAfter, "20")));
                result.put("taAvg", String.valueOf((taMin + taMax) / 2.0));
            }

            if (!cachedMidLandItems.isEmpty()) {
                Map<String, Object> item = cachedMidLandItems.get(0);
                String wfKey = daysAfter <= 7 ? "wf" + daysAfter + "Am" : "wf" + daysAfter;
                result.put("wf", String.valueOf(item.getOrDefault(wfKey, "맑음")));
            }
        } catch (Exception e) {
            System.err.println("⚠️ 기상청 중기 파싱 에러: " + e.getMessage());
        }
        return result;
    }

    // 4. 에어코리아 대기질 (종로구)
    @SuppressWarnings("unchecked")
    private Map<String, Double> getAirKoreaRealTime() {
        LocalDateTime now = LocalDateTime.now();
        // 💡 [캐시 로직] 에어코리아는 30분 이내면 API 안 부르고 기존 값 재사용! (429 에러 완벽 차단)
        if (cachedAirKorea != null && ChronoUnit.MINUTES.between(airKoreaCacheTime, now) < 30) {
            return cachedAirKorea;
        }

        Map<String, Double> result = new HashMap<>();
        try {
            String url = String.format(
                "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=%s&returnType=json&numOfRows=1&pageNo=1&stationName=종로구&dataTerm=DAILY&ver=1.0",
                API_KEY
            );
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            Map<String, Object> respMap = (Map<String, Object>) response.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) respMap.get("body");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) bodyMap.get("items");

            if (itemList != null && !itemList.isEmpty()) {
                Map<String, Object> item = itemList.get(0);
                if (item.get("pm10Value") != null && !item.get("pm10Value").equals("-")) {
                    result.put("pm10", Double.parseDouble((String) item.get("pm10Value")));
                }
                if (item.get("pm25Value") != null && !item.get("pm25Value").equals("-")) {
                    result.put("pm25", Double.parseDouble((String) item.get("pm25Value")));
                }
            }
            // 정상 처리 시 수첩(캐시) 업데이트
            cachedAirKorea = result;
            airKoreaCacheTime = now;
            System.out.println("🔄 [캐시 갱신] 에어코리아 실시간 대기질 API 호출 완료");

        } catch (Exception e) {
            System.err.println("⚠️ 에어코리아 파싱 에러: " + e.getMessage());
        }
        
        // 에러가 났어도 이전에 가지고 있던 수첩의 값이 있으면 그거라도 쓴다!
        return cachedAirKorea != null && result.isEmpty() ? cachedAirKorea : result;
    }
}