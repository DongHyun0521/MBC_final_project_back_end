<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>주차 이용 안내</h2>
      <p>병원을 이용하시는 고객님의 편의를 위해 최선을 다하겠습니다.</p>
    </div>

    <div class="parking-summary">
      <div class="summary-card">
        <div class="icon-box">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2.5">
            <circle cx="12" cy="12" r="10" />
            <polyline points="12 6 12 12 16 14" />
          </svg>
        </div>
        <div class="txt-box">
          <span class="label">초반 30분</span>
          <span class="val">무료 주차</span>
        </div>
      </div>
      <div class="summary-card highlight">
        <div class="icon-box">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2.5">
            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
        </div>
        <div class="txt-box">
          <span class="label">진료 예약 완료</span>
          <span class="val">2시간 무료</span>
        </div>
      </div>
    </div>

    <section class="parking-sec">
      <h3 class="sec-title">
        <span class="title-icon">P</span> 주차 요금 안내
      </h3>
      <table class="parking-tbl">
        <thead>
          <tr>
            <th>구분</th>
            <th>기본 요금</th>
            <th>추가 요금 (30분당)</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="bold">회원 차량</td>
            <td>초반 30분 무료</td>
            <td class="main-txt">1,000원</td>
          </tr>
          <tr>
            <td class="bold">비회원 차량</td>
            <td>초반 30분 무료</td>
            <td class="sub-txt">2,000원</td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="parking-sec">
      <h3 class="sec-title">
        <span class="title-icon">V</span> 예약 환자 혜택
      </h3>
      <div class="benefit-box">
        <div class="b-info">
          <p class="b-title">당일 진료 예약 고객</p>
          <p class="b-desc">진료 예약 상태가 <span class="status-point">‘완료’</span>인 경우에 한함</p>
        </div>
        <div class="b-val">
          <span class="time">2</span>시간 무료
        </div>
      </div>
    </section>

    <div class="parking-notice">
      <h4>이용 안내</h4>
      <ul>
        <li>장애인 및 국가유공자 차량은 관련 법규에 따라 감면 혜택이 적용됩니다.</li>
        <li>홈페이지의 <b>[차량등록]</b> 메뉴를 이용하시면 출차 시 더욱 편리합니다.</li>
        <li>무료 주차 시간 초과 시 무인 정산기에서 결제 후 출차해 주시기 바랍니다.</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.sub-page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 80px 20px;
  font-family: 'Pretendard', sans-serif !important;
}

.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-header h2 {
  font-size: 42px;
  font-weight: 800;
  color: #111;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 18px;
  color: #666;
}

/* 요약 카드 */
.parking-summary {
  display: flex;
  gap: 20px;
  margin-bottom: 50px;
}

.summary-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 30px;
  background: #fff;
  border: 1px solid #eee;
  transition: 0.3s;
}

.summary-card.highlight {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
}

.summary-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.icon-box {
  width: 64px;
  height: 64px;
  background: #f4f7fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.summary-card.highlight .icon-box {
  background: rgba(255, 255, 255, 0.15);
}

.txt-box .label {
  font-size: 16px;
  color: #888;
  display: block;
  margin-bottom: 4px;
}

.summary-card.highlight .label {
  color: #84c2e6;
  font-weight: 600;
}

.txt-box .val {
  font-size: 24px;
  font-weight: 800;
}

/* 섹션 타이틀 */
.parking-sec {
  margin-bottom: 60px;
}

.sec-title {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
}

.title-icon {
  background: #005baa;
  color: #fff;
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 16px;
}

/* 테이블 (기존 컬러 및 정렬 유지) */
.parking-tbl {
  width: 100%;
  border-collapse: collapse;
  border-top: 3px solid #005baa;
}

.parking-tbl th {
  background: #f9fbff;
  padding: 20px;
  font-size: 18px;
  border-bottom: 1px solid #ddd;
  text-align: center;
}

.parking-tbl td {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #eee;
  font-size: 17px;
  color: #555;
}

.parking-tbl td.bold {
  font-weight: 700;
  color: #333;
}

.main-txt {
  color: #005baa;
  font-weight: 800;
}

.sub-txt {
  color: #666;
}

/* 혜택 박스 */
.benefit-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
  background: #fff;
  border: 2px dashed #84c2e6;
}

.b-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.b-desc {
  color: #888;
  font-size: 17px;
}

.status-point {
  color: #fbb900;
  font-weight: 800;
}

.b-val {
  font-size: 22px;
  font-weight: 700;
  color: #005baa;
}

.b-val .time {
  font-size: 38px;
  font-weight: 900;
  margin-right: 4px;
}

/* 안내사항 */
.parking-notice {
  background: #f9f9f9;
  padding: 25px;
  border-radius: 8px;
}

.parking-notice h4 {
  font-size: 18px;
  margin-bottom: 12px;
  color: #333;
}

.parking-notice ul {
  padding-left: 20px;
  color: #777;
  line-height: 1.7;
}
</style>