import axios from "axios";

// Axios 인스턴스 설정
const url = axios.create({
    baseURL: 'http://localhost:8080', // Spring 서버 대문 주소
    withCredentials: true            // 세션(쿠키) 연동 필수
})

// 1. 차량 등록
// 백엔드 주소: /member/vehiRegi
export const addVehiReq = (vehicleData) => {
    return url.post('/member/vehiRegi', vehicleData)
}

// 2. 내 차량 목록 조회
// 백엔드 주소: /member/vehicles
// 백엔드에서 HttpSession("loginId")을 쓰므로 파라미터 없이 호출
export const getVehiReq = () => {
    return url.get('/member/vehicles')
}

// 3. 차량 정보 수정
// 백엔드 주소: /member/vehiUpdate
export const editVehiReq = (vehicleData) => {
    return url.put('/member/vehiUpdate', vehicleData)
}

// 4. 차량 정보 삭제
// 백엔드 주소: /member/vehiDelete
export const delVehiReq = (vNum) => {
    return url.delete(`/member/vehiDelete/${encodeURIComponent(vNum)}`)
}

export default url;