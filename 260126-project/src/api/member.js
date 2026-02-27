import axios from "axios";

// Axios 인스턴스 설정
const url = axios.create({
    baseURL: 'http://localhost:8080', // Spring 서버 주소
    withCredentials: true            // 세션 쿠키 공유를 위해 필수
})

/**
 * [MemController] 영역 - /member
 */

// 1. 로그인 (일반/의료진 공통)
export const loginReq = (userData) => {
    return url.post('/member/login', userData)
}
export const loginMedReq = (userData) => url.post('/member/login', userData)
export const loginAdminReq = (userData) => url.post('/member/login', userData)

// 2. 일반 회원가입
export const regiReq = (userData) => {
    return url.post('/member/regi', userData)
}

// 3. 아이디 중복 확인 (@RequestParam 방식)
export const idCheckReq = (inputId) => {
    return url.get('/member/idcheck', { params: { id: inputId } })
}

// 4. 내 정보 보기 (세션 기반)
export const getMyInfoReq = () => {
    return url.get('/member/mypage')
}

// 5. 내 정보 수정
export const updateInfoReq = (userData) => {
    return url.put('/member/mypageUpdate', userData)
}

// 6. 회원 탈퇴 (del=0 -> 1)
export const withdrawReq = () => {
    return url.delete('/member/withdraw')
}

// 7. 아이디 찾기 (백엔드 /find-id 주소 매칭)
export const findIdReq = (userData) => {
    return url.post('/member/find-id', userData)
}

// 8. 로그아웃 (세션 무효화)
export const logoutReq = () => {
    return url.post('/member/logout')
}


/**
 * [MedController] 영역 - /med
 */

// 9. 의료진 전용 회원가입 (StaffJoinDto 매칭)
export const staffRegiReq = (staffData) => {
    return url.post('/med/staff/register', staffData)
}

/**
 * [AdminController] 영역 - /admin
 */

// 10. 행정직 전용 회원가입 (AdminStaffDto 매칭)
export const adminRegiReq = (adminData) => {
    return url.post('/admin/staff/register', adminData)
}

// 11. 행정 부서 목록 조회
export const getAdminDeptsReq = () => {
  // 백엔드에 만들어둔(혹은 만들 예정인) 행정부서 목록 주소
  return url.get('/admin/dept/list'); 
}

// 12. 행정직 상세 권한 정보 조회 (세션 기반)
// 백엔드 AdminController의 /admin/my-info 주소를 호출
export const getAdminMyInfoReq = () => {
    return url.get('/admin/my-info');
}

export default url;