<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>진료 및 이용 안내</h2>
      <p>환자 중심의 편안하고 전문적인 진료를 약속드립니다.</p>
    </div>

    <div class="content-wrap">
      <section class="info-section">
        <h3>외래 진료 시간표</h3>
        <div class="table-responsive">
          <table class="schedule-table">
            <thead>
              <tr>
                <th>구분</th>
                <th>진료 시간</th>
                <th>접수 및 비고</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <th>평일</th>
                <td>09:00 ~ 18:30</td>
                <td>
                  접수 마감: 17:30<br>
                  <span class="lunch-time">(점심시간: 12:30 ~ 13:30)</span>
                </td>
              </tr>
              <tr>
                <th>토요일</th>
                <td>09:00 ~ 12:30</td>
                <td>오전 진료 (점심시간 없음)</td>
              </tr>
              <tr>
                <th>일요일/공휴일</th>
                <td colspan="2" class="closed">휴진 (응급실 24시간 운영)</td>
              </tr>
            </tbody>
          </table>
        </div>
        <ul class="info-list">
          <li>진료과 사정에 따라 조기 마감되거나 휴진될 수 있으므로 방문 전 확인 바랍니다.</li>
          <li>점심시간 중에는 접수 및 수납 업무가 중단될 수 있습니다.</li>
        </ul>
      </section>

      <section class="info-section emergency-box">
        <h3>🚨 365일 24시간 응급 진료 센터</h3>
        <p>응급의학과 전문의가 상주하여 야간 및 공휴일에도 신속한 대처가 가능합니다.</p>
        <div class="contact-highlight">
          <strong>응급실 직통 전화:</strong> 02-123-4567 (내선 119)
        </div>
      </section>

      <section class="info-section">
        <h3>입원 환자 면회 시간 안내</h3>
        <div class="visiting-hours">
          <dl>
            <dt>일반 병동</dt>
            <dd>
              <strong>평일:</strong> 18:00 ~ 20:00 (1회)<br>
              <strong>주말/공휴일:</strong> 10:00 ~ 12:00, 18:00 ~ 20:00 (2회)
            </dd>
          </dl>
          <dl>
            <dt>중환자실 (ICU)</dt>
            <dd>
              <strong>매일:</strong> 11:00 ~ 11:30, 19:00 ~ 19:30 (각 30분간)
            </dd>
          </dl>
        </div>
        <ul class="info-list small-text">
          <li>감염 예방을 위해 면회객 수를 제한할 수 있으며, 마스크 착용은 필수입니다.</li>
          <li>미취학 아동이나 감기 등 전염성 질환이 있는 분은 면회가 제한됩니다.</li>
        </ul>
      </section>

      <section class="info-section contact-info">
        <h3>대표 전화 및 안내</h3>
        <p>진료 예약 및 기타 문의 사항은 대표 번호를 이용해 주세요.</p>
        <p><strong>대표 전화: 1588-8282 / 02-123-4567</strong></p>
      </section>

    </div>
  </div>
</template>

<style scoped>
/* 전체 레이아웃 */
.sub-page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 60px 20px 100px;
}

.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-header h2 {
  font-size: 42px;
  margin-bottom: 10px;
  color: #222;
}

.page-header p {
  color: #666;
  font-size: 18px;
}

/* 콘텐츠 영역 공통 */
.content-wrap {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.info-section {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee;
}

h3 {
  color: #0171e9;
  font-size: 22px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f1f3f5;
}

/* 테이블 스타일 */
.table-responsive {
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.schedule-table th,
.schedule-table td {
  padding: 15px;
  border: 1px solid #ddd;
  text-align: center;
  font-size: 15px;
  vertical-align: middle;
}

.schedule-table thead th {
  background: #f8f9fa;
  color: #333;
  font-weight: 600;
}

.schedule-table tbody th {
  background: #f1f7fd;
  color: #0171e9;
}

.lunch-time {
  display: block;
  font-size: 13px;
  color: #666;
  margin-top: 5px;
}

.closed {
  color: #e03e2d;
  font-weight: 600;
  background: #fff5f5;
}

/* 리스트 스타일 */
.info-list {
  padding-left: 20px;
  margin: 0;
}

.info-list li {
  margin-bottom: 8px;
  color: #555;
  font-size: 15px;
  line-height: 1.5;
  position: relative;
  list-style: square;
}

.info-list.small-text li {
  font-size: 14px;
  color: #777;
}

/* 응급실 박스 강조 */
.emergency-box {
  border: 2px solid #e03e2d;
  background: #fffafa;
}

.emergency-box h3 {
  color: #e03e2d;
  border-bottom-color: #ffdada;
}

.contact-highlight {
  font-size: 18px;
  color: #e03e2d;
  margin-top: 15px;
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #ffdada;
}

/* 면회 시간 스타일 */
.visiting-hours {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.visiting-hours dl {
  flex: 1;
  min-width: 250px;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin: 0;
}

.visiting-hours dt {
  font-weight: 600;
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.visiting-hours dd {
  margin: 0;
  font-size: 15px;
  color: #555;
  line-height: 1.6;
}

/* 대표 전화 섹션 */
.contact-info {
  text-align: center;
  background: #f1f7fd;
  border: none;
}

.contact-info h3 {
  border-bottom: none;
  margin-bottom: 10px;
}

.contact-info p {
  font-size: 16px;
  margin: 5px 0;
  color: #444;
}

.contact-info strong {
  font-size: 20px;
  color: #0171e9;
}

/* 반응형 처리 */
@media (max-width: 768px) {
  .page-header h2 {
    font-size: 24px;
  }

  .info-section {
    padding: 20px;
  }

  .schedule-table th,
  .schedule-table td {
    padding: 10px;
    font-size: 14px;
  }

  .visiting-hours dl {
    min-width: 100%;
  }
}
</style>