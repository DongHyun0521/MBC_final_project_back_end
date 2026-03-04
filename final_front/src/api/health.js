import axios from 'axios';

// Axios 인스턴스 설정
const url = axios.create({
    baseURL: 'http://localhost:8080', // Spring 서버 주소
    withCredentials: true            // 세션 쿠키 공유를 위해 필수
}) 

// 1. 건강이야기 목록 조회
export const getStoriesReq = (params) => {
  return url.get('/admin/health/list', { params });
};

// 2. 건강이야기 상세 조회
export const getStoryDetailReq = (id) => {
  return url.get(`/admin/health/detail/${id}`);
};

// 3. 건강이야기 등록 (파일 포함 -> FormData)
export const addStoryReq = (formData) => {
  return url.post('/admin/health/write', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// 4. 건강이야기 수정 (파일 포함 -> FormData)
export const editStoryReq = (formData) => {
  return url.post('/admin/health/update', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// 5. 건강이야기 삭제
export const delStoryReq = (id) => {
  return url.put(`/admin/health/delete/${id}`); 
};

// 6. 관리자 정보 조회 (홍보팀 권한 확인용)
export const getAdminInfoReq = () => {
  return url.get('/admin/my-info');
};