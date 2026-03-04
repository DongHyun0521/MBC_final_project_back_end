// 모든 부품을 가져와서 조립하고, 마지막에 index.html에 꽂아주는 '최종 조립 공장'

import { createApp } from 'vue' 
import { createPinia } from 'pinia' // 로그인 정보처럼 모든 페이지에서 공유할 데이터 창고를 만드는 기능 가져오기
//import './style.css'
import App from './App.vue'
import router from './router' // "어느 주소로 가면 어느 화면을 보여줄지" 정해놓은 지도 가져오기

const app = createApp(App) // vue app 실체 생성
const pinia = createPinia() // 데이터를 보관할 실제 창고(Pinia) 인스턴스를 하나 생성

app.use(pinia)  // 위에서 만든 데이터 창고를 우리 앱에 딱 장착함 (이제 모든 조각에서 창고를 쓸 수 있음)
app.use(router) // 네비게이션

app.mount('#app')  // 화면에 시동 걸기
