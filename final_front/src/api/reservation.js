import axios from "axios";

// Axios 인스턴스 설정
const url = axios.create({
    baseURL: 'http://localhost:8080', // Spring 서버 주소
    withCredentials: true             // 세션 공유를 위해 필수
})

/**
 * [MedController] 영역 - 진료 및 의료진 정보
 */

// 1. 진료부서(dept) 목록 가져오기
// 사용처: 진료예약, 진료과 찾기, 회원가입(의료진)
export const getDeptsReq = () => {
    return url.get('/med/dept/list') 
}

// 2. 부서별 의료진 목록 가져오기
// 사용처: 진료예약, 의료진 찾기
export const getDoctorsReq = (deptId) => {
    return url.get(`/med/staff/dept/${deptId}`)
}

// 3. 전체 의료진 목록 가져오기 (필요 시 주석 해제)
export const getAllDoctorsReq = () => {
    return url.get('/med/doctors')
}


/**
 * [Reservation] 영역 - 진료 예약 관리
 */

// 4. 진료 예약 등록하기 (INSERT)
export const addResReq = (reservationData) => {
    return url.post('/med/reservation', reservationData)
}

// 5. 내 예약 내역 조회하기 (CheckReservation.vue, Mypage.vue)
export const getMyResReq = () => {
    return url.get('/med/my-reservations')
}

// 6. 의사별 예약 스케줄 조회
// 백엔드 주소: /med/doctor/schedule/{doctorId}w
export const getDocSchedReq = (staffId) => {
    return url.get(`/med/doctor/schedule/${staffId}`)
}

// 7. 예약 취소하기
// 백엔드 주소: /med/reservation/cancel/{reservationId}
export const cancelResReq = (resId) => {
    return url.put(`/med/reservation/cancel/${resId}`)
}

// 8. 의사가 진료 완료 처리 (백엔드: /med/doctor/reservation/complete/{id})
export const completeResByDocReq = (resId) => {
    return url.put(`/med/doctor/reservation/complete/${resId}`) 
}

// 9. 의사가 예약 강제 취소 (백엔드: /med/doctor/reservation/cancel/{id})
export const forceCancelResByDocReq = (resId) => {
    return url.put(`/med/doctor/reservation/cancel/${resId}`)
}

export default url;