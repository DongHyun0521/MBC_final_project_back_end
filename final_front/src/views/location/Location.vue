<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>오시는 길</h2>
      <p>서울에스병원으로 오시는 빠르고 편한 길을 안내해 드립니다</p>
    </div>

    <div class="map-container-wrap">
      <div class="map-photo-placeholder">
        <img src="@/assets/지도.jpg" alt="병원 위치 지도" class="map-image">

        <div class="map-info-card">
          <div class="info-item">
            <span class="label">주소</span>
            <p class="addr">경기도 성남시 수정구 금토로80번길 56, 위든타워 C동 서울에스병원</p>
          </div>
          <div class="info-item">
            <span class="label">대표전화</span>
            <p class="tel">1588-8282</p>
          </div>
        </div>
      </div>
    </div>

    <div class="transport-info">
      <section class="info-section">
        <h3 class="sec-title">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2.5"
            class="title-icon">
            <rect x="7" y="5" width="10" height="14" rx="0" ry="0" />
            <path d="M7 11h10M9 15h0M15 15h0" />
          </svg>
          지하철 이용 시
        </h3>
        <div class="route-box">
          <p class="route-main">신분당선 · 경강선 <b class="point-blue">판교역</b> 하차</p>
          <p class="route-sub">2번 출구(동편) 육교 건너편 버스 정류장에서 310번, 55번 환승</p>
        </div>
      </section>

      <section class="info-section">
        <h3 class="sec-title">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2.5"
            class="title-icon">
            <path
              d="M19 17h2l.64-2.54c.24-.95.36-1.92.36-2.9V10c0-1.1-.9-2-2-2H4c-1.1 0-2 .9-2 2v1.56c0 .98.12 1.95.36 2.9L3 17h2" />
            <path d="M7 11h10M5 17v2a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1v-2M15 17v2a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1v-2" />
          </svg>
          버스 이용 시
        </h3>
        <div class="route-box">
          <p class="route-main"><b class="point-blue">위든타워</b> 정류장 하차</p>
          <ul class="bus-list">
            <li><span class="bus-tag">일반버스</span> 310, 382</li>
            <li><span class="bus-tag">마을버스</span> 55</li>
          </ul>
        </div>
      </section>
    </div>

    <section class="notice-section">
      <div class="notice-box">
        <h4>주차 안내 및 내원 유의사항</h4>
        <ul class="notice-list">
          <li><strong>주차 공간:</strong> 위든타워 지하 1층 ~ 지하 3층 전용 주차장 이용 가능합니다</li>
          <li><strong>주차 요금:</strong> 외래 진료 환자 3시간 무료, 입퇴원 당일 8시간 무료 주차를 지원합니다</li>
          <li><strong>주차권 확인:</strong> 수납 창구에서 진료비 영수증 또는 차량 번호를 등록해 주시기 바랍니다</li>
          <li><strong>대중교통 권장:</strong> 평일 오전 시간대는 주차장이 혼잡하오니 가급적 대중교통 이용을 부탁드립니다</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 전체 기본 설정 (Pretendard & No Curve) */
.sub-page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 80px 20px;
  font-family: 'Pretendard', sans-serif !important;
}

/* 메인 타이틀 (42px / 18px) */
.page-header {
  text-align: center;
  margin-bottom: 70px;
}

.page-header h2 {
  font-size: 42px;
  font-weight: 800;
  color: #111;
  margin-bottom: 15px;
  letter-spacing: -1px;
}

.page-header p {
  font-size: 18px;
  color: #666;
  font-weight: 400;
}

/* 지도 영역 레이아웃 (직선형) */
.map-container-wrap {
  position: relative;
  margin-bottom: 60px;
}

.map-photo-placeholder {
  position: relative;
  width: 100%;
  height: 500px;
  background: #f0f3f7;
  border: 1px solid #ddd;
}

.map-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 지도 위 정보 카드 (직선형) */
.map-info-card {
  position: absolute;
  bottom: 30px;
  left: 30px;
  background: #fff;
  padding: 35px;
  border-radius: 0;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border-top: 5px solid #005baa;
  z-index: 10;
}

.info-item {
  margin-bottom: 15px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .label {
  font-size: 14px;
  color: #005baa;
  font-weight: 700;
  display: block;
  margin-bottom: 5px;
}

.info-item p {
  font-size: 18px;
  color: #333;
  font-weight: 600;
  margin: 0;
  line-height: 1.4;
}

/* 교통 안내 섹션 (직선형) */
.transport-info {
  display: flex;
  flex-direction: column;
  gap: 30px;
  margin-bottom: 60px;
}

.info-section {
  padding: 45px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 0;
}

.sec-title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 800;
  color: #111;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f1f3f5;
}

.title-icon {
  margin-right: 12px;
}

.route-main {
  font-size: 20px;
  color: #333;
  font-weight: 700;
  margin-bottom: 12px;
}

.route-sub {
  font-size: 17px;
  color: #666;
  line-height: 1.6;
}

.point-blue {
  color: #005baa;
}

/* 버스 리스트 디자인 (직선형) */
.bus-list {
  list-style: none;
  padding: 0;
  margin-top: 20px;
}

.bus-list li {
  font-size: 17px;
  color: #444;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.bus-tag {
  background: #f1f7fd;
  color: #005baa;
  font-size: 13px;
  font-weight: 700;
  padding: 5px 12px;
  margin-right: 15px;
  min-width: 80px;
  text-align: center;
  border-radius: 0;
}

/* 하단 유의사항 섹션 (직선형) */
.notice-section {
  border: 1px solid #eee;
}

.notice-box {
  padding: 40px;
  background: #f8f9fa;
  border-left: 6px solid #005baa;
}

.notice-box h4 {
  font-size: 20px;
  color: #111;
  margin-bottom: 20px;
  font-weight: 700;
}

.notice-list {
  padding-left: 20px;
  margin: 0;
}

.notice-list li {
  font-size: 16px;
  color: #555;
  margin-bottom: 12px;
  line-height: 1.6;
  list-style-type: square;
}

/* 반응형 처리 */
@media (max-width: 768px) {
  .page-header h2 {
    font-size: 32px;
  }

  .map-photo-placeholder {
    height: 350px;
  }

  .map-info-card {
    position: static;
    padding: 25px;
    box-shadow: none;
    border-left: 5px solid #005baa;
    border-top: none;
  }

  .info-section {
    padding: 30px 20px;
  }

  .sec-title {
    font-size: 20px;
  }
}
</style>