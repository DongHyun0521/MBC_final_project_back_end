<!-- <template>
    <div class="kiosk-container">
        <header class="kiosk-header">
            <h3>{{ $route.meta.title || '서울에스병원' }}</h3>
        </header>

        <main class="kiosk-main">
            <router-view />
        </main>

        <footer class="kiosk-footer">
            <p>Copyright &copy; 서울에스병원</p>
        </footer>
    </div>
</template>

<style scoped>
.kiosk-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    width: 100%;
}

.kiosk-header {
    background: #404347;
    color: #fff;
    padding: 20px;
    text-align: center;
}

.kiosk-main {
    flex: 1;
    overflow-y: auto;
    background: #fff;
}

.kiosk-footer {
    background: #f8f9fa;
    padding: 15px;
    text-align: center;
    font-size: 13px;
    color: #999;
}
</style> -->

<template>
    <div class="kiosk-wrapper">
        <!-- <header class="kiosk-header">
            <div class="header-inner">
                <div class="brand-group">
                    <!-- <span class="hospital-name">서울에스병원</span>
                    <span class="hospital-sub">SEOUL S HOSPITAL</span>
                    <img class="main-logo-img" src="@/assets/txtlogo2.png" alt="s_hospital 로고 텍스트" />
                </div>
                <div class="header-divider"></div>
                <h3 class="page-title">{{ $route.meta.title || '주차 시스템' }}</h3>
            </div>
        </header> -->

        <main class="kiosk-main">
            <div class="content-container">
                <router-view />
            </div>
        </main>

        <footer class="kiosk-footer">
            <div class="footer-line"></div>
            <div class="footer-text">
                <p class="help-text">도움이 필요하시면 <span>호출 버튼</span>을 눌러주세요</p>
                <p class="copyright">Copyright © 2026 SEOUL S HOSPITAL. All rights reserved.</p>
            </div>
        </footer>
    </div>
</template>

<style scoped>
.kiosk-wrapper {
    display: flex;
    flex-direction: column;
    width: 100vw;
    height: 100vh;
    background: linear-gradient(180deg, #ffffff 0%, #f0f7ff 100%);
    overflow: hidden;
    /* 스크롤 방지 */
    margin: 0;
    padding: 0;
}

.kiosk-header {
    height: 100px;
    /* 높이 고정 */
    padding: 0 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border-bottom: 2px solid rgba(132, 194, 230, 0.2);
    z-index: 10;
}

.main-logo-img{
    width: 150px;
    height: auto;

}

.header-inner {
    display: flex;
    align-items: center;
}

.brand-group {
    display: flex;
    flex-direction: column;
}

.hospital-name {
    font-size: 1.8rem;
    font-weight: 900;
    color: #005baa;
    line-height: 1;
}

.hospital-sub {
    font-size: 0.8rem;
    font-weight: 700;
    color: #84c2e6;
    letter-spacing: 1px;
    margin-top: 5px;
}

.header-divider {
    width: 2px;
    height: 35px;
    background: #fbb900;
    margin: 0 30px;
}

.page-title {
    font-family: 'pretendard';
    font-size: 22px;
    font-weight: 700;
    color: #444;
    margin: 0;
}

.kiosk-main {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    padding: 0; /* 내부 패딩을 줄여서 공간 확보 */
    overflow: hidden; /* 자식이 삐져나가지 못하게 차단 */
}

.content-container {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.kiosk-footer {
    height: 120px; /* 푸터 높이 고정 */
    flex-shrink: 0; /* 콘텐츠가 많아도 푸터가 찌그러지지 않게 고정 */
    background: transparent;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding-bottom: 10px;
    z-index: 20;
}

.footer-line {
    width: 80%;
    height: 1px;
    background: linear-gradient(90deg, transparent, #84c2e6, transparent);
    margin-bottom: 15px;
}

.help-text {
    font-size: 1.4rem;
    font-weight: 500;
    color: #666;
    letter-spacing: -0.88px;
    margin-bottom: 8px;
    margin-top: 10px;
    font-family: 'pretendard';
    text-align: center;
}

.help-text span {
    color: #fbb900;
    text-decoration: underline;
}

.copyright {
    font-size: 0.8rem;
    color: #666;
    letter-spacing: 0.5px;
}
</style>