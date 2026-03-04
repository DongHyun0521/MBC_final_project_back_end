<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>진료 예약 및 절차</h2>
      <p>병원 방문 전 예약 방법을 확인하시면 더욱 편리합니다</p>
    </div>

    <section class="reservation-methods">
      <div class="method-card">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2">
            <rect x="5" y="2" width="14" height="20" rx="0" ry="0"></rect>
            <line x1="12" y1="18" x2="12" y2="18"></line>
          </svg>
        </div>
        <h3>온라인 예약</h3>
        <p>24시간 언제 어디서나<br>모바일과 PC로 간편하게</p>
        <router-link to="/reservation" class="btn-primary">예약하기</router-link>
      </div>

      <div class="method-card">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2">
            <path
              d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z">
            </path>
          </svg>
        </div>
        <h3>전화 접수</h3>
        <p>전문 상담원 연결<br><strong>02-1234-5678</strong></p>
        <span class="tag">평일 09:00 ~ 17:30</span>
      </div>

      <div class="method-card">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
            <polyline points="9 22 9 12 15 12 15 22"></polyline>
          </svg>
        </div>
        <h3>방문 접수</h3>
        <p>원무과 창구 방문<br>당일 현장 접수 가능</p>
        <span class="tag">신분증 지참 필수</span>
      </div>
    </section>

    <section class="process-section">
      <h3>진료 진행 순서</h3>
      <div class="step-container">
        <div class="step-item">
          <div class="square">1</div>
          <span>접수/수납</span>
        </div>
        <svg class="flow-arrow" viewBox="0 0 24 24">
          <path d="M5 12h14M12 5l7 7-7 7" stroke="#ccc" stroke-width="2" fill="none" />
        </svg>
        <div class="step-item">
          <div class="square">2</div>
          <span>진료과 방문</span>
        </div>
        <svg class="flow-arrow" viewBox="0 0 24 24">
          <path d="M5 12h14M12 5l7 7-7 7" stroke="#ccc" stroke-width="2" fill="none" />
        </svg>
        <div class="step-item">
          <div class="square">3</div>
          <span>검사/처방</span>
        </div>
        <svg class="flow-arrow" viewBox="0 0 24 24">
          <path d="M5 12h14M12 5l7 7-7 7" stroke="#ccc" stroke-width="2" fill="none" />
        </svg>
        <div class="step-item highlight">
          <div class="square">4</div>
          <span>귀가/입원</span>
        </div>
      </div>
    </section>

    <section class="notice-section">
      <div class="notice-box">
        <h4>진료 예약 시 주의사항</h4>
        <ul class="notice-list">
          <li><strong>예약 시간 준수:</strong> 원활한 진료를 위해 예약 시간 10분 전까지 내원해 주시기 바랍니다</li>
          <li><strong>신분증 지참:</strong> 보건복지부 규정에 따라 진료 시 본인 확인을 위한 신분증 제시가 의무화되었습니다</li>
          <li><strong>예약 변경/취소:</strong> 예약 취소는 최소 1일 전까지 홈페이지 또는 전화를 통해 가능합니다</li>
          <li><strong>건강검진 예약:</strong> 검진 예약은 일반 진료 예약과 별도로 운영되오니 전용 창구를 이용해 주세요</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 폰트 및 기본 설정 (border-radius 제거) */
.sub-page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 80px 20px;
  font-family: 'Pretendard', sans-serif;
  /* Pretendard 통일 */
}

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

/* 예약 방법 카드 (직각형) */
.reservation-methods {
  display: flex;
  gap: 25px;
  margin-bottom: 90px;
}

.method-card {
  flex: 1;
  background: #fff;
  padding: 45px 20px;
  border: 1px solid #eee;
  text-align: center;
  border-radius: 0;
  /* 곡선 제거 */
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.02);
}

.icon-wrapper {
  width: 60px;
  height: 60px;
  margin: 0 auto 25px;
}

.icon-wrapper svg {
  width: 100%;
  height: 100%;
}

.method-card h3 {
  font-size: 22px;
  color: #222;
  margin-bottom: 15px;
}

.method-card p {
  font-size: 16px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 20px;
}

.tag {
  font-size: 14px;
  color: #005baa;
  background: #eef6ff;
  padding: 5px 12px;
  font-weight: 600;
  display: inline-block;
  border-radius: 0;
  /* 곡선 제거 */
}

.btn-primary {
  display: inline-block;
  background: #005baa;
  color: #fff;
  padding: 12px 30px;
  font-weight: 600;
  font-size: 15px;
  text-decoration: none;
  border-radius: 0;
  /* 곡선 제거 */
  transition: background 0.2s;
}

.btn-primary:hover {
  background: #0056b3;
}

/* 진료 절차 섹션 (직각형) */
.process-section {
  background: #f9fbff;
  padding: 60px 40px;
  border-radius: 0;
  /* 곡선 제거 */
  text-align: center;
  margin-bottom: 50px;
}

.process-section h3 {
  font-size: 24px;
  margin-bottom: 45px;
  color: #333;
}

.step-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  width: 140px;
}

/* 원형에서 사각형(square)으로 변경 */
.square {
  width: 50px;
  height: 50px;
  background: #fff;
  border: 2px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  color: #999;
  font-size: 18px;
  border-radius: 0;
}

.step-item span {
  font-size: 18px;
  font-weight: 700;
  color: #444;
}

.step-item.highlight .square {
  border-color: #005baa;
  background: #005baa;
  color: #fff;
}

.step-item.highlight span {
  color: #005baa;
}

.flow-arrow {
  width: 30px;
  height: 30px;
  margin-bottom: 35px;
}

/* 주의사항 섹션 (직각형) */
.notice-section {
  border: 1px solid #eee;
  border-radius: 0;
}

.notice-box {
  padding: 35px;
  background: #fff;
  border-left: 6px solid #005baa;
}

.notice-box h4 {
  font-size: 20px;
  color: #222;
  margin-bottom: 20px;
}

.notice-list {
  padding-left: 20px;
  margin: 0;
}

.notice-list li {
  font-size: 16px;
  color: #555;
  margin-bottom: 12px;
  line-height: 1.5;
  list-style-type: square;
}

.notice-list li strong {
  color: #333;
}

/* 반응형 */
@media (max-width: 900px) {
  .page-header h2 {
    font-size: 32px;
  }

  .reservation-methods {
    flex-direction: column;
  }

  .step-container {
    flex-direction: column;
    gap: 20px;
  }

  .flow-arrow {
    transform: rotate(90deg);
    margin: 0;
  }

  .step-item {
    width: 100%;
  }
}
</style>