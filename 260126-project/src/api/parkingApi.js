import axios from 'axios';

const parkingAxios = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true
});

const API = '/parking/spot';

export default {
  getAllSpots:     ()       => parkingAxios.get(`${API}/list`),
  getFloorSpots:  (floor)  => parkingAxios.get(`${API}/list/floor/${floor}`),
  dummyEntry:     ()       => parkingAxios.post(`${API}/entry/dummy`),
  exit:           (spotId) => parkingAxios.post(`${API}/exit/${spotId}`),
  exitAll:        ()       => parkingAxios.post(`${API}/reset/all`),
  exitPreview:    (spotId) => parkingAxios.get(`${API}/exit/preview/${spotId}`),
  searchVehicle:  (vNum)   => parkingAxios.get(`${API}/searchNum`, { params: { vehicleNum: vNum } }),
  countAvailable: (floor)  => parkingAxios.get(`${API}/count/available`, { params: floor ? { floor } : {} }),

  // 사전정산
  prepay:         (spotId) => parkingAxios.post(`${API}/prepay/${spotId}`),

  // 출차 정산 확인
  checkExit:      (vNum)   => parkingAxios.get(`${API}/exit/check`, { params: { vehicleNum: vNum } }),

  // 출차 실행
  processExit:    (vNum)   => parkingAxios.post(`${API}/exit/process`, null, { params: { vehicleNum: vNum } }),

  // 추가정산 결제
  payAdditional:  (parkingLogId, amount) => parkingAxios.post(`${API}/exit/pay-additional`, null, {
    params: { parkingLogId, amount }
  })
};
