<template>
  <div class="vehicle-search">
    <h3 class="title">🔍 차량 검색</h3>

    <!-- 검색 입력 -->
    <div class="search-box">
      <input
        type="text"
        v-model="searchInput"
        placeholder="차량번호 입력 (예: 3456)"
        @keyup.enter="searchVehicle"
      />
      <button @click="searchVehicle" :disabled="!searchInput.trim()">검색</button>
    </div>

    <!-- 검색 결과 -->
    <div class="result-list" v-if="results.length > 0">
      <div
        class="result-card"
        v-for="r in results"
        :key="r.spotId"
        :class="{ selected: selected?.spotId === r.spotId }"
        @click="selectResult(r)"
      >
        <div class="result-info">
          <div class="vehicle-num">{{ r.vehicleNum }}</div>
          <div class="location">B{{ r.floor }}층 {{ r.zone }}{{ r.spotNumber }}</div>
        </div>
        <div class="arrow">→</div>
      </div>
    </div>

    <!-- 결과 없음 -->
    <div class="no-results" v-if="searched && results.length === 0">
      검색 결과가 없습니다.
    </div>

    <!-- 선택 상세 (exitPreview 데이터 포함) -->
    <div class="detail" v-if="selected">
      <h4>🚘 차량 상세 정보</h4>

      <div class="detail-row">
        <span class="dlbl">차량번호</span>
        <span class="dval">{{ selected.vehicleNum }}</span>
      </div>
      <div class="detail-row">
        <span class="dlbl">주차위치</span>
        <span class="dval loc">B{{ selected.floor }}층 {{ selected.zone }}{{ selected.spotNumber }}</span>
      </div>

      <div class="divider"></div>

      <div class="detail-row">
        <span class="dlbl">입차시간</span>
        <span class="dval">{{ detail.entryTime || '-' }}</span>
      </div>
      <div class="detail-row">
        <span class="dlbl">현재시간</span>
        <span class="dval">{{ detail.nowTime || '-' }}</span>
      </div>
      <div class="detail-row">
        <span class="dlbl">주차시간</span>
        <span class="dval">{{ detail.totalMinutes != null ? detail.totalMinutes + '분' : '-' }}</span>
      </div>

      <div class="divider"></div>

      <div class="detail-row fee-row">
        <span class="dlbl">정산금액</span>
        <span class="dval fee" :class="{ free: detail.isFree }">
          {{ detail.isFree ? '무료 (30분 이내)' : formatWon(detail.amount) }}
        </span>
      </div>
      <div class="detail-row" v-if="detail.isMember">
        <span class="dlbl">회원할인</span>
        <span class="dval member-badge">회원 요금 적용</span>
      </div>

      <div class="detail-loading" v-if="detailLoading">정보 조회 중...</div>

      <!-- 사전정산 결과 메시지 -->
      <div class="prepay-result" v-if="prepayResult">
        <div class="prepay-success">
          ✅ {{ prepayResult.message }}
        </div>
        <div class="prepay-info">
          {{ prepayResult.graceMinutes }}분 이내에 출차해주세요.
        </div>
      </div>

      <div class="btn-group">
        <button class="show-btn" @click="showOnMap">📍 지도에서 보기</button>
        <button
          class="prepay-btn"
          @click="doPrepay"
          :disabled="prepayLoading || prepayResult"
        >
          {{ prepayLoading ? '정산 중...' : prepayResult ? '정산 완료' : '💳 사전정산' }}
        </button>
      </div>
    </div>

    <!-- 돌아가기 -->
    <button class="back-btn" @click="goBack">← 주차 현황으로</button>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api/parkingApi';

export default {
  name: 'VehicleSearch',
  setup() {
    const router = useRouter();

    const searchInput = ref('');
    const results     = ref([]);
    const selected    = ref(null);
    const searched    = ref(false);
    const detailLoading = ref(false);
    const prepayLoading = ref(false);
    const prepayResult  = ref(null);
    const detail = reactive({
      entryTime: null,
      nowTime: null,
      totalMinutes: null,
      amount: null,
      isFree: false,
      isMember: false
    });

    const resetDetail = () => {
      detail.entryTime = null;
      detail.nowTime = null;
      detail.totalMinutes = null;
      detail.amount = null;
      detail.isFree = false;
      detail.isMember = false;
      prepayResult.value = null;
    };

    const formatWon = (n) => {
      if (n == null) return '-';
      return n.toLocaleString() + '원';
    };

    const formatTime = (dt) => {
      if (!dt) return '-';
      return dt.replace('T', ' ').substring(0, 19);
    };

    const searchVehicle = async () => {
      if (!searchInput.value.trim()) return;
      searched.value = true;
      selected.value = null;
      resetDetail();

      try {
        const { data } = await api.searchVehicle(searchInput.value);
        results.value = data;
      } catch {
        results.value = [];
      }
    };

    const selectResult = async (r) => {
      selected.value = r;
      resetDetail();

      if (r.entryTime) {
        detail.entryTime = r.entryTime;
      }

      detailLoading.value = true;
      try {
        const { data } = await api.exitPreview(r.spotId);
        detail.entryTime = formatTime(data.entryTime);
        detail.nowTime = formatTime(data.nowTime);
        detail.totalMinutes = data.totalMinutes;
        detail.amount = data.amount;
        detail.isFree = data.free;
        detail.isMember = data.member;
      } catch {
        detail.nowTime = new Date().toLocaleString('ko-KR');
      } finally {
        detailLoading.value = false;
      }
    };

    const doPrepay = async () => {
      if (!selected.value) return;

      const confirmMsg = detail.isFree
        ? '무료 주차입니다. 사전정산을 진행하시겠습니까?'
        : `${formatWon(detail.amount)}을 사전정산 하시겠습니까?`;

      if (!confirm(confirmMsg)) return;

      prepayLoading.value = true;
      try {
        const { data } = await api.prepay(selected.value.spotId);
        prepayResult.value = data;
      } catch (e) {
        alert(e.response?.data?.message || '사전정산에 실패했습니다.');
      } finally {
        prepayLoading.value = false;
      }
    };

    const showOnMap = () => {
      if (!selected.value) return;
      router.push({
        name: 'ParkingStatus',
        query: { hl: JSON.stringify({
          floor: selected.value.floor,
          zone: selected.value.zone,
          spotNumber: selected.value.spotNumber
        })}
      });
    };

    const goBack = () => { router.push({ name: 'ParkingStatus' }); };

    return {
      searchInput, results, selected, searched,
      detail, detailLoading,
      prepayLoading, prepayResult,
      searchVehicle, selectResult, doPrepay, showOnMap, goBack, formatWon
    };
  }
};
</script>

<style scoped>
.vehicle-search {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: #fff; border-radius: 16px; padding: 25px;
  box-shadow: 0 2px 20px rgba(0,0,0,0.08); max-width: 500px; margin: 0 auto;
}
.title { font-size: 18px; font-weight: 600; color: #1a1a1a; margin-bottom: 20px; }

.search-box { display: flex; gap: 10px; margin-bottom: 20px; }
.search-box input {
  flex: 1; padding: 12px 16px; border: 2px solid #e5e5e5; border-radius: 10px; font-size: 14px; outline: none;
}
.search-box input:focus { border-color: #007AFF; }
.search-box button {
  padding: 12px 20px; background: #007AFF; color: #fff; border: none;
  border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer;
}
.search-box button:hover { background: #0056b3; }
.search-box button:disabled { opacity: 0.5; cursor: not-allowed; }

.result-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 20px; }
.result-card {
  display: flex; justify-content: space-between; align-items: center;
  padding: 15px; background: #f5f5f7; border-radius: 12px; cursor: pointer;
  border: 2px solid transparent;
}
.result-card:hover { background: #e8e8ed; }
.result-card.selected { border-color: #FF9500; background: #FFF8E1; }
.result-info { display: flex; flex-direction: column; gap: 4px; }
.vehicle-num { font-size: 16px; font-weight: 600; color: #1a1a1a; }
.location { font-size: 13px; color: #86868b; }
.arrow { font-size: 18px; color: #007AFF; }

.no-results { text-align: center; padding: 20px; color: #86868b; font-size: 14px; }

.detail { background: #f0f7ff; padding: 22px; border-radius: 14px; margin-bottom: 15px; }
.detail h4 { font-size: 15px; font-weight: 600; color: #007AFF; margin-bottom: 16px; }
.detail-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.dlbl { font-size: 13px; color: #86868b; }
.dval { font-size: 14px; font-weight: 600; color: #1a1a1a; }
.dval.loc { color: #007AFF; }
.dval.fee { color: #FF3B30; font-size: 16px; }
.dval.fee.free { color: #34C759; }
.member-badge {
  font-size: 12px; color: #007AFF; background: #e3f2fd;
  padding: 2px 8px; border-radius: 6px;
}

.divider { border-top: 1px solid #d5e3f0; margin: 12px 0; }

.detail-loading { text-align: center; color: #86868b; font-size: 13px; padding: 8px 0; }

.prepay-result {
  background: #e8f5e9; border-radius: 10px; padding: 14px; margin-top: 12px;
}
.prepay-success {
  font-size: 15px; font-weight: 600; color: #2e7d32; margin-bottom: 6px;
}
.prepay-info {
  font-size: 13px; color: #558b2f;
}

.btn-group {
  display: flex; gap: 10px; margin-top: 12px;
}
.show-btn {
  flex: 1; padding: 12px; background: #007AFF; color: #fff; border: none;
  border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer;
}
.show-btn:hover { background: #0056b3; }

.prepay-btn {
  flex: 1; padding: 12px; background: #FF9500; color: #fff; border: none;
  border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer;
}
.prepay-btn:hover { background: #e08600; }
.prepay-btn:disabled { background: #34C759; cursor: default; }

.back-btn {
  width: 100%; padding: 12px; background: #f5f5f7; color: #86868b; border: none;
  border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer; margin-top: 10px;
}
.back-btn:hover { background: #e8e8ed; color: #1a1a1a; }
</style>
