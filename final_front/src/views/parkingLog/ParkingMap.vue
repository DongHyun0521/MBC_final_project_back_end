<template>
  <div class="parking-map-wrapper">
    <div class="floor-tabs">
      <button 
        v-for="floor in 3" :key="floor" 
        :class="['floor-btn', { active: currentFloor === floor }]"
        @click="currentFloor = floor"
      >
        지하 {{ floor }}층
      </button>
    </div>

    <div class="legend-area">
      <div class="legend-item">
        <span class="led-box empty-led"></span>빈자리 ({{ emptyCount }}대)
      </div>
      <div class="legend-item">
        <span class="led-box parked-led"></span>주차됨 ({{ parkedCount }}대)
      </div>
    </div>

    <div class="parking-lot-container">
      <div class="indicator entrance-indicator">
        <span v-if="currentFloor === 1">⬇️ 주차장 입/출구 ⬆️</span>
        <span v-else>⬇️ 지하 {{ currentFloor - 1 }}층 ⬆️</span>
      </div>

      <div v-if="currentFloor < 3" class="indicator ramp-indicator">
        <span>⬇️ 지하 {{ currentFloor + 1 }}층 ⬆️</span>
      </div>

      <div class="parking-grid">
        <div v-for="row in 4" :key="row" class="parking-row">
          <div class="spots-container">
            <div 
              v-for="col in 10" :key="col" 
              :class="['spot', getSpot(row, col)?.isParked ? 'parked clickable' : 'empty']"
              @click="openModal(getSpot(row, col))"
            >
              <div class="spot-led"></div>
              <span class="spot-name">{{ getRowLetter(row) }}-{{ col }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="selectedSpot" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>주차 상세 정보</h2>
          <button class="close-btn" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <table class="info-table">
            <tr>
              <th>주차 구역</th>
              <td class="highlight-text">지하 {{ selectedSpot.parkingFloor }}층 {{ getRowLetter(selectedSpot.parkingRow) }}-{{ selectedSpot.parkingColumn }}</td>
            </tr>
            <tr>
              <th>차량 번호</th>
              <td class="plate-text">{{ selectedSpot.vehicleNum || '정보 없음' }}</td>
            </tr>
            <tr>
              <th>입차 시간</th>
              <td>{{ formatDateTime(selectedSpot.entryTime) }}</td>
            </tr>
            <tr>
              <th>총 주차 시간</th>
              <td class="duration-text">{{ calculateDuration(selectedSpot.entryTime) }}</td>
            </tr>
          </table>
        </div>
        <div class="modal-footer">
          <button class="confirm-btn" @click="closeModal">확인</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

const currentFloor = ref(1); 
const parkingSpots = ref([]); 

const selectedSpot = ref(null); 

// 💡 팝업창에서 실시간으로 시간을 업데이트하기 위한 반응형 변수
const currentTime = ref(new Date());

const emptyCount = computed(() => parkingSpots.value.filter(s => s.parkingFloor === currentFloor.value && !s.isParked).length);
const parkedCount = computed(() => parkingSpots.value.filter(s => s.parkingFloor === currentFloor.value && s.isParked).length);

const getRowLetter = (row) => String.fromCharCode(64 + row);

const getSpot = (row, col) => {
  return parkingSpots.value.find(s => s.parkingFloor === currentFloor.value && s.parkingRow === row && s.parkingColumn === col);
};

const openModal = (spot) => {
  if (spot && spot.isParked) {
    selectedSpot.value = spot;
  }
};

const closeModal = () => {
  selectedSpot.value = null;
};

const formatDateTime = (timeStr) => {
  if (!timeStr) return '-';
  const date = new Date(timeStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 💡 입차 시간과 현재 시간을 비교하여 "X시간 Y분"을 계산하는 함수
const calculateDuration = (timeStr) => {
  if (!timeStr) return '-';
  
  // 브라우저 호환성(Safari 등)을 위해 날짜 포맷 안전하게 변환
  const safeTimeStr = timeStr.replace(/-/g, '/').replace('T', ' ');
  const start = new Date(safeTimeStr);
  const diffMs = currentTime.value - start; // 현재 시간에서 입차 시간을 뺌 (밀리초)

  if (diffMs < 0) return '방금 입차함';

  const diffMins = Math.floor(diffMs / (1000 * 60)); // 총 분 계산
  const days = Math.floor(diffMins / (60 * 24)); // 일 계산
  const hours = Math.floor((diffMins % (60 * 24)) / 60); // 시간 계산
  const minutes = diffMins % 60; // 분 계산

  let result = '';
  if (days > 0) result += `${days}일 `;
  result += `${hours}시간 ${minutes}분`;
  
  return result;
};

let intervalId = null; 

const fetchParkingSpots = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/parking/spots');
    parkingSpots.value = res.data;
  } catch (error) {
    console.error("주차장 데이터를 불러오지 못했습니다.", error);
  }
};

onMounted(() => {
  fetchParkingSpots(); 
  intervalId = setInterval(() => {
    fetchParkingSpots();
    // 💡 3초마다 주차장 데이터를 갱신할 때, 현재 시간도 갱신하여 팝업창 주차 시간을 실시간으로 올림!
    currentTime.value = new Date();
  }, 3000); 
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});
</script>

<style scoped>
.parking-map-wrapper { padding: 40px; background-color: #ffffff; min-height: 100vh; font-family: 'Pretendard', sans-serif; display: flex; flex-direction: column; align-items: center; }
.floor-tabs { display: flex; gap: 15px; margin-bottom: 25px; }
.floor-btn { padding: 12px 40px; font-size: 22px; font-weight: 800; border: 2px solid #005baa; background-color: #ffffff; color: #005baa; border-radius: 8px; cursor: pointer; transition: all 0.2s; }
.floor-btn.active { background-color: #005baa; color: #ffffff; box-shadow: 0 4px 10px rgba(0, 91, 170, 0.2); }
.legend-area { display: flex; gap: 30px; margin-bottom: 30px; font-size: 18px; font-weight: 600; color: #333333; }
.legend-item { display: flex; align-items: center; gap: 10px; }
.led-box { width: 18px; height: 18px; border-radius: 50%; }
.empty-led { background-color: #10b981; box-shadow: 0 2px 5px rgba(16, 185, 129, 0.4); }
.parked-led { background-color: #ef4444; box-shadow: 0 2px 5px rgba(239, 68, 68, 0.4); }
.parking-lot-container { background-color: #e5e7eb; padding: 80px 60px 80px 220px; border-radius: 12px; position: relative; box-shadow: 0 10px 30px rgba(0,0,0,0.05); border: 5px solid #000000; }

.indicator { 
  position: absolute; 
  left: 20px; 
  background-color: #ffeb3b; 
  color: #000000; 
  padding: 12px 18px; 
  font-weight: 900; 
  font-size: 18px; 
  border-radius: 6px; 
  box-shadow: 0 4px 8px rgba(0,0,0,0.15); 
  border: 2px solid #d4b106; 
  text-align: center;
}
.entrance-indicator { top: 20px; }
.ramp-indicator { bottom: 20px; }

.parking-grid { display: flex; flex-direction: column; gap: 30px; }
.spots-container { display: flex; gap: 12px; background-color: #d1d5db; padding: 15px; border-radius: 8px; }
.spot { width: 80px; height: 120px; display: flex; flex-direction: column; justify-content: center; align-items: center; border-radius: 6px; position: relative; transition: all 0.3s ease; background-color: #ffffff; border: 2px solid #9ca3af; overflow: hidden; }
.spot-led { position: absolute; top: 0; left: 0; width: 100%; height: 12px; }
.spot.empty { border-color: #10b981; }
.spot.empty .spot-led { background-color: #10b981; }
.spot.empty .spot-name { color: #059669; }
.spot.parked { border-color: #ef4444; background-color: #fef2f2; }
.spot.parked .spot-led { background-color: #ef4444; }
.spot.parked .spot-name { color: #b91c1c; }
.spot-name { font-size: 22px; font-weight: 900; letter-spacing: 1px; margin-top: 10px; }

.spot.clickable { cursor: pointer; }
.spot.clickable:hover { transform: translateY(-2px); box-shadow: 0 4px 10px rgba(239, 68, 68, 0.3); }

.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.6); display: flex; justify-content: center; align-items: center; z-index: 9999; }
.modal-content { background-color: #ffffff; width: 450px; border-radius: 12px; overflow: hidden; box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { transform: translateY(-30px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
.modal-header { background-color: #005baa; color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { margin: 0; font-size: 20px; font-weight: 700; }
.close-btn { background: none; border: none; color: white; font-size: 24px; cursor: pointer; }
.modal-body { padding: 25px; }
.info-table { width: 100%; border-collapse: collapse; }
.info-table th { text-align: left; padding: 12px 0; color: #6b7280; width: 35%; font-size: 16px; border-bottom: 1px solid #e5e7eb; }
.info-table td { padding: 12px 0; font-weight: 600; font-size: 16px; color: #111827; border-bottom: 1px solid #e5e7eb; }
.highlight-text { color: #005baa !important; font-weight: 800 !important; }
.plate-text { font-size: 22px !important; letter-spacing: 2px; color: #111827; }

/* 💡 추가된 부분: 총 주차 시간 강조 스타일 */
.duration-text { font-size: 18px !important; color: #fb6900 !important; font-weight: 800 !important; }

.modal-footer { padding: 15px 25px 25px; display: flex; justify-content: flex-end; }
.confirm-btn { background-color: #005baa; color: white; border: none; padding: 10px 25px; border-radius: 6px; font-size: 16px; font-weight: 600; cursor: pointer; }
.confirm-btn:hover { background-color: #004488; }
</style>