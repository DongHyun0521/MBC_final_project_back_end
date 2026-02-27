

DROP TABLE IF EXISTS member_vehicle;	-- 
DROP TABLE IF EXISTS reservation;		-- 
DROP TABLE IF EXISTS payment;			-- 

DROP TABLE IF EXISTS med_staff;			-- 
DROP TABLE IF EXISTS med_dept;			-- 

DROP TABLE IF EXISTS notice;			-- 
DROP TABLE IF EXISTS faq;				-- 
DROP TABLE IF EXISTS voc;				-- 
DROP TABLE IF EXISTS health_story;		-- 

DROP TABLE IF EXISTS admin_staff;		-- 
DROP TABLE IF EXISTS admin_dept;		-- 

DROP TABLE IF EXISTS parking_spot;		-- 
DROP TABLE IF EXISTS parking_log;		-- 

DROP TABLE IF EXISTS mem;				-- 

-- ==================================================

-- 회원 정보
CREATE TABLE mem (
    mem_id SERIAL PRIMARY KEY,						-- PK
    id VARCHAR(20) UNIQUE NOT NULL,					-- 아이디 (6~16자, 영어 소문자+숫자만 사용가능)
    password VARCHAR(100) NOT NULL,					-- 비밀번호 (6~16자, 영어 대/소문자 1개이상, 특수문자 1개이상)
    name VARCHAR(30) NOT NULL,						-- 이름
    birthday INTEGER NOT NULL,						-- 생년월일 (숫자 8자리)
    gender INTEGER NOT NULL,						-- 성별 (남:1, 여:2)
    address VARCHAR(100) NOT NULL,					-- 주소
	address_detail VARCHAR(100),			-- 상세 주소
    phone_number VARCHAR(20) NOT NULL,				-- 전화번호
    email VARCHAR(50) NOT NULL,						-- 이메일
    del INTEGER NOT NULL DEFAULT 0,					-- 탈퇴 여부
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 가입 일시
);
-- 회원 차량 정보
CREATE TABLE member_vehicle (
    vehicle_id SERIAL PRIMARY KEY,					-- PK
    mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK (mem.mem_id)
    vehicle_num VARCHAR(20) NOT NULL,				-- 차량 번호 (회원이 등록)
    vehicle_type VARCHAR(20) NOT NULL,				-- 차종
    fuel_type VARCHAR(20) NOT NULL,					-- 연료
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 등록 일시
);
-- 주차장 기록
CREATE TABLE parking_log (
    parking_log_id SERIAL PRIMARY KEY,				-- PK
    vehicle_num VARCHAR(20) NOT NULL,				-- 차량 번호 (OCR 결과)
    entry_time TIMESTAMP NOT NULL DEFAULT now(),	-- 입차 시간
    exit_time TIMESTAMP,							-- 출차 시간
    is_member BOOLEAN NOT NULL DEFAULT FALSE,		-- 회원 여부
    payment_status BOOLEAN NOT NULL DEFAULT FALSE	-- 결제 여부
);
-- 요금 결제 기록
CREATE TABLE payment (
    pay_id SERIAL PRIMARY KEY,						-- PK
    parking_log_id INTEGER NOT NULL
		REFERENCES parking_log(parking_log_id),		-- FK (parking_log.parking_log_id)
    mem_id INTEGER
		REFERENCES mem(mem_id) ON DELETE SET NULL,	-- FK (mem.mem_id)
    amount INTEGER CHECK (amount >= 0),				-- 결제 금액
    pay_method VARCHAR(50),							-- 결제 수단
    pay_date TIMESTAMP NOT NULL DEFAULT now()		-- 결제 일시
);
-- 주차장 구역 상태
CREATE TABLE parking_spot (
    spot_id SERIAL PRIMARY KEY,					-- PK
    parking_log_id INTEGER DEFAULT NULL
		REFERENCES parking_log(parking_log_id),	-- FK (parking_log.parking_log_id)
    floor INTEGER NOT NULL,						-- 층 (1, 2, ...)
    zone VARCHAR(1) NOT NULL,					-- 구역 (A, B, ...)
	spot_number INTEGER NOT NULL,				-- 번호 (1, 2, ...)
	--distance_from_entrance INTEGER NOT NULL,	-- 입구로부터의 거리
	is_parked BOOLEAN NOT NULL DEFAULT FALSE	-- 주차 여부
);
-- 의료 부서
CREATE TABLE med_dept (
    med_dept_id SERIAL PRIMARY KEY,		-- PK
	med_dept_name VARCHAR(50) NOT NULL,	-- 의료 부서명
	dept_location VARCHAR(100),			-- 부서 위치
	dept_phone_number VARCHAR(20)		-- 부서 전화번호
);
-- 의료진
CREATE TABLE med_staff (
	med_staff_id SERIAL PRIMARY KEY,				-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK (mem.mem_id)
	role VARCHAR(20) NOT NULL,						-- 직업
	license_number VARCHAR(50) NOT NULL,			-- 의료진 등록 번호
	status VARCHAR(20) NOT NULL DEFAULT '재직',		-- 재직 상태
	med_dept_id INTEGER
		REFERENCES med_dept(med_dept_id),			-- FK (med_dept.med_dept_id)
	spot_id INTEGER
		REFERENCES parking_spot(spot_id),			-- FK (parking_spot.spot_id)
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 회원가입 일시
);
-- 진료 예약
CREATE TABLE reservation (
	reservation_id SERIAL PRIMARY KEY,		-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id),				-- FK (mem.mem_id)
	med_dept_id INTEGER NOT NULL
		REFERENCES med_dept(med_dept_id),	-- FK (med_dept.med_dept_id)
	doctor_id INTEGER NOT NULL
		REFERENCES med_staff(med_staff_id),	-- FK (med_staff.med_staff_id)
	reservation_date INTEGER NOT NULL,						-- 예약 날짜
	reservation_time VARCHAR(30) NOT NULL,					-- 예약 시간
	reservation_type VARCHAR(20) NOT NULL,					-- 예약 종류
	visit_type VARCHAR(20) NOT NULL DEFAULT '초진',			-- 초진/재진
	reservation_status VARCHAR(20) NOT NULL DEFAULT '예약',	-- 예약 상태
	reservation_memo VARCHAR(1000),							-- 예약 관련 메모
	reservation_made_time TIMESTAMP NOT NULL DEFAULT now()	-- 예약 당시 시간
);
-- 행정 부서
CREATE TABLE admin_dept (
	admin_dept_id SERIAL PRIMARY KEY,			-- PK
	admin_dept_name VARCHAR(50) NOT NULL,		-- 행정 부서 이름
	dept_location VARCHAR(100),					-- 부서 위치
	dept_phone_number VARCHAR(20)				-- 부서 전화번호
);
-- 행정직
CREATE TABLE admin_staff (
	admin_staff_id SERIAL PRIMARY KEY,				-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK (mem.mem_id)
	rank VARCHAR(20),								-- 직급
	emp_number VARCHAR(50) NOT NULL,				-- 사원번호
	status VARCHAR(20) NOT NULL DEFAULT '재직',		-- 재직 상태
	admin_dept_id INTEGER
		REFERENCES admin_dept(admin_dept_id),		-- FK (admin_dept.admin_dept_id)
	spot_id INTEGER
		REFERENCES parking_spot(spot_id),			-- FK (parking_spot.spot_id)
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 가입 일시
);
-- 고객의소리
CREATE TABLE voc (
	voc_id SERIAL PRIMARY KEY,						-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id),						-- FK (mem.mem_id)
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	upload_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	
	admin_staff_id INTEGER
		REFERENCES admin_staff(admin_staff_id),		-- FK (admin_staff.admin_staff_id)
	answer_content VARCHAR(4000),					-- 답변 내용
	answer_write_date TIMESTAMP,					-- 답변 작성일시
	answer_status BOOLEAN NOT NULL DEFAULT FALSE,	-- 답변 여부
	
	del INTEGER NOT NULL DEFAULT 0,	-- 글 삭제 여부
	delete_date TIMESTAMP			-- 삭제 버튼 눌린 시간
);
-- 공지사항
CREATE TABLE notice (
	notice_id SERIAL PRIMARY KEY,					-- PK
	admin_staff_id INTEGER NOT NULL
		REFERENCES admin_staff(admin_staff_id),		-- FK (admin_staff.admin_staff_id)
	top_fix BOOLEAN NOT NULL DEFAULT FALSE,			-- 상단 고정 여부
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	thumbnail_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	read_count INTEGER NOT NULL DEFAULT 0,			-- 조회수
	del INTEGER NOT NULL DEFAULT 0					-- 삭제 여부
);
-- FAQ
CREATE TABLE faq (
	faq_id SERIAL PRIMARY KEY,						-- PK
	admin_staff_id INTEGER NOT NULL
		REFERENCES admin_staff(admin_staff_id),		-- FK (admin_staff.admin_staff_id)
	category VARCHAR(50) NOT NULL,					-- 카테고리
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	del INTEGER NOT NULL DEFAULT 0					-- 삭제 여부
);
-- 건강이야기
CREATE TABLE health_story (
	health_story_id SERIAL PRIMARY KEY,				-- PK
	admin_staff_id INTEGER NOT NULL
		REFERENCES admin_staff(admin_staff_id),		-- FK(admin_staff.admin_staff_id)
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	thumbnail_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
	read_count INTEGER NOT NULL DEFAULT 0,			-- 조회수
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	del INTEGER NOT NULL DEFAULT 0					-- 삭제 여부
);

-- ==================================================

DELETE FROM reservation;
DELETE FROM voc;
DELETE FROM notice;
DELETE FROM faq;
DELETE FROM health_story;
DELETE FROM member_vehicle;
DELETE FROM payment;

DELETE FROM med_staff;
DELETE FROM admin_staff;
DELETE FROM parking_spot;

DELETE FROM parking_log;
DELETE FROM med_dept;
DELETE FROM admin_dept;
DELETE FROM mem;

-- ==================================================

SELECT * FROM mem;				-- 회원
SELECT * FROM med_staff;		-- 의료진
SELECT * FROM admin_staff;		-- 행정 직원

SELECT * FROM med_dept;			-- 의료 부서 (미리 INSERT)
SELECT * FROM admin_dept;		-- 행정 부서 (미리 INSERT)

SELECT * FROM member_vehicle;	-- 회원 차량

SELECT * FROM parking_log;		-- 주차 기록
SELECT * FROM parking_spot;		-- 주차 위치 (미리 INSERT)
SELECT * FROM payment;			-- 결제

SELECT * FROM reservation;		-- 예약

SELECT * FROM voc;				-- 고객의소리
SELECT * FROM notice;			-- 공지사항
SELECT * FROM faq;				-- FAQ
SELECT * FROM health_story;		-- 건강이야기

-- ==================================================

INSERT INTO med_dept (med_dept_name, dept_location, dept_phone_number) VALUES 
	('내과', '본관 102호', '02-1111-1112'),
	('정형외과', '본관 103호', '02-1111-1113'),
	('신경외과', '본관 201호', '02-1111-1114'),
	('소아청소년과', '별관 101호', '02-1111-1115'),
	('이비인후과', '별관 102호', '02-1111-1116'),
	('피부과', '별관 201호', '02-1111-1117'),
	('안과', '본관 202호', '02-1111-1118'),
	('치과', '별관 202호', '02-1111-1119'),
	('정신건강의학과', '별관 301호', '02-1111-1120'),
	('응급의학과', '본관 100호', '02-1111-1119');

INSERT INTO admin_dept (admin_dept_name, dept_location, dept_phone_number) VALUES 
	('총무과', '본관 B101호', '02-2222-2223'),
	('인사팀', '본관 B102호', '02-2222-2224'),
	('원무과', '본관 100호(로비)', '02-2222-2225'),
	('재무회계팀', '본관 B103호', '02-2222-2226'),
	('시설관리팀', '별관 B101호', '02-2222-2227'),
	('의무기록팀', '별관 105호', '02-2222-2228'),
	('홍보팀', '본관 501호', '02-2222-2229'),
	('보안팀', '본관 100호(보안실)', '02-2222-2230');
	
-- ==================================================

-- 회원 1
INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
VALUES ('mem123', 'Pass123!', '김회원', 19901123, 1, '서울시 강남구 XX오피스텔', '108호', '010-1453-7897', 'user01@naver.com');

-- 회원 2
INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
VALUES ('mem456', 'Pass123!', '이회원', 19850728, 2, '서울시 중구 XX빌라', '304호', '010-2734-8905', 'user02@kakao.com');

-- 회원 3
INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
VALUES ('mem789', 'Pass123!', '박회원', 20010521, 1, '서울시 서초구 OO오피스텔', '1104호', '010-0324-5843', 'user03@gmail.com');

-- ==================================================

-- 행정:원무 1
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin111', 'Pass123!', '최원무', 19700829, 2, '서울시 성북구 KK빌라', '404호', '010-3475-1048', 'admin31@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '대리', 'A25850492', '재직', 3
FROM inserted_admin;

-- 행정:원무 2
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin222', 'Pass123!', '정원무', 19910923, 1, '서울시 강북구 PP아파트', '1104호', '010-1254-6941', 'admin32@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '팀장', 'L10583085', '재직', 3
FROM inserted_admin;

-- 행정:원무 3
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin333', 'Pass123!', '강원무', 19870421, 1, '서울시 노원구 MM오피스텔', '2104호', '010-1474-5082', 'admin33@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '사원', 'Q19450235', '재직', 3
FROM inserted_admin;

-- ==================================================

-- 행정:홍보 1
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin777', 'Pass123!', '조홍보', 19921204, 1, '서울시 광진구 WW아파트', '205호', '010-3643-4587', 'admin71@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '주임', 'G02934543', '재직', 7
FROM inserted_admin;

-- 행정:홍보 2
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin888', 'Pass123!', '윤홍보', 19940929, 2, '서울시 중랑구 TT아파트', '1503호', '010-1465-4954', 'admin72@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '대리', 'D10394857', '재직', 7
FROM inserted_admin;

-- 행정:홍보 3
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin999', 'Pass123!', '장홍보', 19890726, 2, '서울시 동대문구 XX아파트', '908호', '010-0645-1490', 'admin73@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '팀장', 'M10495029', '재직', 7
FROM inserted_admin;

-- ==================================================

WITH inserted_doc AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('doc123', 'Pass123!', '임의사', 19800505, 1, '서울시 서초구 강남대로', '202호', '010-9999-1111', 'doc9999@hospital.com')
    RETURNING mem_id
)
INSERT INTO med_staff (mem_id, role, license_number, status, med_dept_id)
SELECT mem_id, '의사', '130101', '재직', 1
FROM inserted_doc;

-- 의사 (10개 부서별 10명씩)
WITH dept_data AS (
    SELECT * FROM (VALUES
        (1, '내과'), (2, '정형외과'), (3, '신경외과'), (4, '소아청소년과'), (5, '이비인후과'),
        (6, '피부과'), (7, '안과'), (8, '치과'), (9, '정신건강의학과'), (10, '응급의학과')
    ) AS t(dept_id, dept_name)
),
surnames AS (
    SELECT * FROM (VALUES
        (1, '김'), (2, '이'), (3, '박'), (4, '최'), (5, '정'),
        (6, '강'), (7, '조'), (8, '윤'), (9, '장'), (10, '임')
    ) AS t(idx, surname)
),
gen_doc AS (
    SELECT
        d.dept_id,
        s.idx AS doc_idx,
        'doctor' || d.dept_id || LPAD(s.idx::TEXT, 2, '0') AS gen_id, -- 예: doctor101, doctor1001
        'aaaa!A' AS gen_pw,                                           -- 비밀번호 통일
        s.surname || d.dept_name AS gen_name,                         -- 예: 김내과, 이내과
        19800000 + (d.dept_id * 100) + s.idx AS gen_birth,            -- 생년월일 자동 생성
        (s.idx % 2) + 1 AS gen_gender,                                -- 성별 교차 배정
        '서울시 메디컬구 의사동' AS gen_addr,
        d.dept_id || '길 ' || s.idx || '호' AS gen_addr_detail,
        '010-2222-' || LPAD(((d.dept_id - 1) * 10 + s.idx)::TEXT, 4, '0') AS gen_phone,
        'doctor' || d.dept_id || LPAD(s.idx::TEXT, 2, '0') || '@shospital.com' AS gen_email, -- 이메일 지정
        '13' || LPAD(d.dept_id::TEXT, 2, '0') || LPAD(s.idx::TEXT, 2, '0') AS gen_license   -- 면허번호 지정
    FROM dept_data d
    CROSS JOIN surnames s
),
inserted_mem AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    SELECT gen_id, gen_pw, gen_name, gen_birth, gen_gender, gen_addr, gen_addr_detail, gen_phone, gen_email
    FROM gen_doc
    RETURNING mem_id, id
)
INSERT INTO med_staff (mem_id, role, license_number, status, med_dept_id)
SELECT m.mem_id, '의사', g.gen_license, '재직', g.dept_id
FROM inserted_mem m
JOIN gen_doc g ON m.id = g.gen_id;

-- ==================================================

-- 주차 자리


-- ==================================================

SELECT * FROM parking_log;
-- 비회원 차량
WITH new_log AS (
    INSERT INTO parking_log (vehicle_num, entry_time, is_member, payment_status)
    VALUES ('18하6294', now() - INTERVAL '3 hours', false, false)
    RETURNING parking_log_id
)
UPDATE parking_spot
SET parking_log_id = (SELECT parking_log_id FROM new_log),
    is_parked = true
WHERE spot_id = 1;
-- 회원 차량 (진료X)
WITH new_log AS (
    INSERT INTO parking_log (vehicle_num, entry_time, is_member, payment_status)
    VALUES ('62서0424', now() - INTERVAL '3 hours', false, false)
    RETURNING parking_log_id
)
UPDATE parking_spot
SET parking_log_id = (SELECT parking_log_id FROM new_log),
    is_parked = true
WHERE spot_id = 2;
-- 회원 차량 (진료O)
WITH new_log AS (
    INSERT INTO parking_log (vehicle_num, entry_time, is_member, payment_status)
    VALUES ('89루0528', now() - INTERVAL '3 hours', false, false)
    RETURNING parking_log_id
)
UPDATE parking_spot
SET parking_log_id = (SELECT parking_log_id FROM new_log),
    is_parked = true
WHERE spot_id = 3;

-- ==================================================

-- FAQ 병원이용 카테고리 4개마다 10개씩
INSERT INTO faq (admin_staff_id, category, title, content, write_date) VALUES

-- 카테고리:병원이용 10개
(1, '병원이용', '진료 시간은 어떻게 되나요?', '평일(월~금)은 오전 9시부터 오후 6시까지이며, 토요일은 오전 9시부터 오후 1시까지(오전 진료) 운영됩니다. 일요일 및 법정 공휴일은 외래 진료가 휴진이오나, 응급의료센터는 365일 24시간 정상 운영됩니다.', now() - INTERVAL '40 days'),
(2, '병원이용', '응급실은 24시간 운영하나요?', '네, 본원 응급의료센터는 365일 24시간 전문의와 전문 응급 간호 인력이 상주하여 응급 상황에 대처하고 있습니다. 야간 및 공휴일에도 본관 1층 응급의료센터 입구를 통해 언제든 진료를 받으실 수 있습니다.', now() - INTERVAL '39 days'),
(3, '병원이용', '주차 요금 및 무료 주차 안내가 궁금합니다.', '외래 진료 환자의 경우 진료 당일 4시간 무료 주차가 가능하며, 입/퇴원 환자는 당일에 한해 24시간 무료 주차가 제공됩니다. 일반 방문객은 최초 30분은 무료이며, 이후 30분당 2,000원의 요금이 부과됩니다. 출차 전 무인 수납기를 통해 사전 정산하시면 더욱 편리합니다.', now() - INTERVAL '38 days'),
(1, '병원이용', '처음 방문했는데 진료 접수는 어떻게 해야 하나요?', '본원에 처음 방문하신 경우, 본관 1층 로비에 위치한 ''처음 오신 분'' 창구로 오셔서 진료신청서를 작성하신 후 신분증과 함께 제출해 주시기 바랍니다. 타 병원의 진료의뢰서를 가져오신 경우 접수 시 함께 제출해 주시면 됩니다.', now() - INTERVAL '37 days'),
(2, '병원이용', '병문안(면회) 시간은 어떻게 되나요?', '환자의 안정과 감염 예방을 위해 병문안 시간은 평일 오후 6시~8시, 주말 및 공휴일은 오전 10시~12시 / 오후 6시~8시로 제한하여 운영하고 있습니다. 감염성 질환자나 임산부, 노약자의 병문안은 엄격히 제한하고 있으니 양해 부탁드립니다.', now() - INTERVAL '36 days'),
(1, '병원이용', '병원 내에 약국이 있나요?', '의약분업 및 관련 의료법에 따라 병원 내부에는 외래 환자를 위한 일반 약국이 없습니다. 진료 후 수납 창구에서 처방전을 발급받으신 뒤, 병원 정문 외부에 위치한 인근 약국을 이용해 주시기 바랍니다.', now() - INTERVAL '35 days'),
(3, '병원이용', '휠체어나 유모차 대여가 가능한가요?', '거동이 불편하신 환자분들과 유아를 위해 본관 1층 안내 데스크에서 휠체어와 유모차를 무료로 대여해 드리고 있습니다. 신분증을 맡기시면 원내 진료 시간 동안 자유롭게 이용하실 수 있습니다.', now() - INTERVAL '34 days'),
(2, '병원이용', '외국인 환자 진료도 가능한가요?', '네, 가능합니다. 본원에서는 외국인 환자분들의 원활한 진료를 돕기 위해 ''국제진료센터''를 운영하고 있습니다. 전문 통역 코디네이터(영어, 중국어, 일본어)가 상주하여 진료 예약부터 동행, 수납까지 1:1 맞춤 안내를 제공합니다.', now() - INTERVAL '33 days'),
(1, '병원이용', '병원 내에서 물건을 잃어버렸는데 어디서 찾을 수 있나요?', '병원 내에서 분실 및 습득된 물품은 본관 1층 고객지원센터(보안실)에서 통합하여 보관 및 관리하고 있습니다. 분실물을 찾으실 때는 신분증을 지참하시고 보안실로 방문해 주시기 바랍니다.', now() - INTERVAL '32 days'),
(3, '병원이용', '입원할 때 준비해야 할 개인 물품은 무엇인가요?', '환자복과 기본 침구류는 병원에서 제공됩니다. 환자분께서는 개인 세면도구(수건, 칫솔, 치약 등), 미끄러지지 않는 슬리퍼, 물통(텀블러), 속옷, 화장지 등을 준비해 주시기 바랍니다. 복용 중이신 약이 있다면 처방전과 함께 꼭 지참해 주세요.', now() - INTERVAL '31 days'),

-- 카테고리:홈페이지 이용 10개
(2, '홈페이지 이용', '홈페이지 회원가입 시 어떤 장점이 있나요?', '회원으로 가입하시면 언제 어디서나 간편하게 온라인 진료 예약 및 취소가 가능하며, 본인의 과거 진료 내역, 처방 내역, 검사 결과 등을 안전하게 조회하실 수 있습니다. 또한 맞춤형 건강 정보 및 병원 소식도 받아보실 수 있습니다.', now() - INTERVAL '30 days'),
(1, '홈페이지 이용', '홈페이지에서 진료 예약은 어떻게 하나요?', '로그인 후 메인 화면의 [진료예약] 메뉴를 클릭해 주세요. 원하시는 진료과와 담당 의료진, 그리고 방문 가능한 날짜와 시간을 선택하시면 즉시 예약이 완료되며, 안내 알림톡이 발송됩니다.', now() - INTERVAL '29 days'),
(3, '홈페이지 이용', '온라인으로 예약한 진료를 변경/취소하고 싶습니다.', '로그인 후 우측 상단의 [마이페이지] - [예약 조회/변경/취소] 메뉴로 들어가시면 예약 내역을 확인하실 수 있습니다. 해당 메뉴에서 직접 날짜를 변경하거나 예약을 취소할 수 있습니다.', now() - INTERVAL '28 days'),
(1, '홈페이지 이용', '로그인 비밀번호를 잊어버렸습니다.', '로그인 화면 하단의 ''아이디/비밀번호 찾기'' 버튼을 클릭해 주세요. 본인 명의의 휴대폰 또는 이메일 인증을 거치면 임시 비밀번호를 발급받으실 수 있습니다. 로그인 후 반드시 새로운 비밀번호로 변경해 주시기 바랍니다.', now() - INTERVAL '27 days'),
(2, '홈페이지 이용', '아이디를 잊어버렸는데 어떻게 찾나요?', '로그인 화면의 ''아이디 찾기'' 메뉴를 이용해 주세요. 가입 시 등록하신 휴대폰 번호 인증이나 아이핀(I-PIN) 인증을 통해 본인 확인이 완료되면 가입된 아이디를 바로 확인하실 수 있습니다.', now() - INTERVAL '26 days'),
(3, '홈페이지 이용', '회원가입 시 본인 인증 단계에서 자꾸 실패합니다.', '본인 인증 실패 시, 입력하신 정보(이름, 생년월일, 통신사, 휴대폰 번호)가 개통된 통신사의 실제 등록 정보와 정확히 일치하는지 확인해 주십시오. 최근 개명하셨거나 통신사를 변경하신 경우 인증 시스템 반영에 며칠이 소요될 수 있습니다.', now() - INTERVAL '25 days'),
(1, '홈페이지 이용', '과거 진료 내역은 홈페이지 어디서 확인하나요?', '로그인 후 [마이페이지] - [나의 진료내역] 메뉴를 클릭하시면 최근 진료받으신 날짜, 진료과, 담당 주치의 정보를 한눈에 조회하실 수 있습니다.', now() - INTERVAL '24 days'),
(2, '홈페이지 이용', '홈페이지에서 개인정보(연락처, 주소 등) 수정은 어떻게 하나요?', '로그인 후 [마이페이지] - [개인정보 수정] 메뉴에 접속하여 비밀번호를 한 번 더 입력하시면 정보를 수정할 수 있습니다. 중요한 예약 및 검사 안내가 누락되지 않도록 연락처와 주소는 항상 최신 상태로 유지해 주시기 바랍니다.', now() - INTERVAL '23 days'),
(3, '홈페이지 이용', '모바일(스마트폰)에서도 홈페이지 이용 및 예약이 가능한가요?', '네, 본원 홈페이지는 스마트폰 환경에 최적화된 모바일 웹을 제공하고 있습니다. 또한, 앱스토어나 플레이스토어에서 병원 공식 ''모바일 앱''을 다운로드하시면 모바일 진료카드, 결제 등 더 편리한 스마트 기능을 이용하실 수 있습니다.', now() - INTERVAL '22 days'),
(1, '홈페이지 이용', '회원 탈퇴는 어떻게 하나요?', '로그인 후 [마이페이지] - [개인정보 수정] 메뉴 하단에 있는 ''회원 탈퇴'' 버튼을 클릭하시면 진행됩니다. 단, 웹 회원 탈퇴를 하시더라도 병원에서의 실제 진료 기록(의무기록)은 의료법에 따라 안전하게 분리 보관됩니다.', now() - INTERVAL '21 days'),

-- 카테고리:증명서발급 10개
(2, '증명서발급', '진단서나 소견서 발급은 어떻게 받나요?', '의료법에 따라 진단서 및 소견서는 반드시 담당 의사와 대면 진료를 한 후에만 발급이 가능합니다. 외래 진료 시 의사에게 발급을 요청하시고, 원무과 제증명 창구에서 수납 및 직인을 받아 수령하시면 됩니다.', now() - INTERVAL '20 days'),
(1, '증명서발급', '가족이나 대리인이 증명서를 대신 발급받을 수 있나요?', '환자 본인 방문이 어려운 경우 대리인 발급이 가능합니다. 단, 환자의 신분증(사본 가능), 대리인의 신분증, 환자 자필 서명이 포함된 ''동의서 및 위임장'', 그리고 ''가족관계증명서''를 반드시 지참하셔야 합니다.', now() - INTERVAL '19 days'),
(3, '증명서발급', '홈페이지(온라인)로 발급 가능한 서류는 무엇인가요?', '진단서처럼 의사 서명이 필요한 서류는 내원 필수이나, 의사 서명이 필요 없는 진료비 영수증, 진료비 세부내역서, 진료확인서, 연말정산용 의료비 납입 증명서 등은 홈페이지를 통해 무료로 온라인 발급 및 출력이 가능합니다.', now() - INTERVAL '18 days'),
(1, '증명서발급', '영문 진단서 발급도 가능한가요?', '네, 영문 진단서 발급이 가능합니다. 진료 시 담당 의사에게 미리 요청해 주셔야 하며, 여권에 기재된 영문 이름 스펠링을 원무과에 정확히 알려주셔야 합니다. 작성 및 발급에 국문 서류보다 약간의 시간이 더 소요될 수 있습니다.', now() - INTERVAL '17 days'),
(2, '증명서발급', '과거에 발급받은 진단서를 재발급할 때 비용이 드나요?', '최초 발급 시에는 종류에 따라 1~2만 원의 발급 비용이 발생하지만, 완전히 동일한 내용의 진단서를 재발급(사본) 받으시는 경우에는 통상적으로 1장당 1,000원의 제증명 수수료만 부과됩니다.', now() - INTERVAL '16 days'),
(3, '증명서발급', '입퇴원 확인서 발급 절차를 알려주세요.', '퇴원 수속을 하실 때 병동 간호사나 원무과 퇴원 창구에 발급을 요청하시면 즉시 발급해 드립니다. 퇴원 이후에 필요하신 경우에는 신분증을 지참하여 1층 제증명 창구를 방문하시거나 홈페이지 온라인 제증명 서비스를 이용해 주세요.', now() - INTERVAL '15 days'),
(1, '증명서발급', '영상자료(MRI, CT, X-ray 등) CD 복사는 어떻게 하나요?', '타 병원 제출 등을 위해 영상 자료 복사가 필요하신 경우, 진료 시 의사에게 미리 복사 처방(오더)을 받으셔야 합니다. 이후 무인 CD 복사기 또는 영상의학과 창구에서 복사 비용(1장당 약 1~2만 원)을 수납하신 후 수령하실 수 있습니다.', now() - INTERVAL '14 days'),
(2, '증명서발급', '연말정산용 의료비 납입 증명서는 어떻게 발급하나요?', '본원에서의 진료 내역은 매년 초 국세청 연말정산 간소화 서비스(홈택스)로 자동 전송됩니다. 따라서 별도로 발급받으실 필요가 없으나, 국세청 자료에서 누락된 건이 있다면 본원 홈페이지에서 직접 출력하시거나 원무과에서 발급받으실 수 있습니다.', now() - INTERVAL '13 days'),
(3, '증명서발급', '미성년자 자녀의 진단서 발급 시 필요한 서류는 무엇인가요?', '만 19세 미만 미성년 환자의 법정대리인(부모 등)이 방문하실 경우, 방문하시는 부모님의 신분증 원본과 환자와의 관계를 증명할 수 있는 ''가족관계증명서'' 또는 ''주민등록등본''(주민번호 모두 표기)을 반드시 지참하셔야 합니다.', now() - INTERVAL '12 days'),
(1, '증명서발급', '팩스나 이메일로 제증명 서류를 받을 수 있나요?', '죄송합니다. 환자의 민감한 개인정보 보호 및 의료법 위반 소지가 있어, 의무기록 사본과 제증명 서류는 팩스나 이메일 발송이 엄격히 금지되어 있습니다. 본인/대리인이 내원하시거나 보안이 적용된 홈페이지를 통해서만 발급이 가능합니다.', now() - INTERVAL '11 days'),

-- 카테고리:건강검진 10개
(2, '건강검진', '건강검진 예약은 어떻게 하나요?', '건강검진은 홈페이지의 ''건강검진센터'' 메뉴를 통해 온라인으로 예약하시거나, 건강검진 전용 상담 콜센터를 통해 예약하실 수 있습니다. 예약이 확정되면 카카오톡 알림톡으로 준비사항과 문진표가 발송됩니다.', now() - INTERVAL '10 days'),
(3, '건강검진', '건강검진 전날 금식 등 주의사항이 궁금합니다.', '정확한 검사를 위해 검진 전날 밤 9시부터는 반드시 금식하셔야 합니다. 식사는 물론 물, 껌, 사탕, 커피, 담배 등도 일절 금합니다. 대장내시경 예약자는 검진 3일 전부터 안내받으신 식단 조절 수칙을 꼭 지켜주셔야 합니다.', now() - INTERVAL '9 days'),
(1, '건강검진', '검진 결과는 언제 나오며 어떻게 확인하나요?', '검진 결과는 검사일로부터 약 1~2주일 이내에 작성하실 때 선택하신 방법(우편 또는 이메일)으로 발송해 드립니다. 홈페이지 회원이신 경우 PC나 모바일 앱의 ''검진결과 조회'' 메뉴에서 상시 확인이 가능합니다.', now() - INTERVAL '8 days'),
(2, '건강검진', '수면(진정) 내시경 후 바로 운전해도 되나요?', '아니요, 절대 불가합니다. 수면 내시경 후 충분히 휴식을 취하더라도 당일에는 약기운이 남아있어 판단력과 반사 신경이 크게 저하됩니다. 사고 예방을 위해 검사 당일 자가 운전은 절대 금지하며 대중교통을 이용해 주십시오.', now() - INTERVAL '7 days'),
(3, '건강검진', '올해 제가 국가건강검진 대상자인지 어떻게 확인하나요?', '올해 국가검진 대상자 여부는 국민건강보험공단 홈페이지(''건강iN'')에서 공인인증서 로그인 후 조회하시거나, 본원 건강검진센터 안내 데스크로 문의해 주시면 대상 여부 및 해당되는 암 검진 항목을 확인해 드립니다.', now() - INTERVAL '6 days'),
(1, '건강검진', '여성의 경우 생리 중일 때 검진이 가능한가요?', '생리 중에는 소변 검사 및 부인과(자궁경부암 등) 검사에 혈액이 섞여 정확한 결과를 얻기 어렵습니다. 따라서 생리가 완전히 끝난 후 5~7일 이후로 검진 일정을 변경하시는 것을 권장합니다.', now() - INTERVAL '5 days'),
(2, '건강검진', '검진 당일, 평소 복용 중인 약은 어떻게 해야 하나요?', '고혈압 약이나 심장 약은 검진 당일 새벽 6시경에 최소한의 물과 함께 복용하세요. 단, 당뇨약(인슐린 주사 포함)은 저혈당 위험이 있으므로 당일 아침 절대 복용 금지입니다. 아스피린 등 항응고제는 내시경 시 지혈을 방해하므로 주치의 상담 후 1주일 전 중단해야 합니다.', now() - INTERVAL '4 days'),
(1, '건강검진', '임산부도 건강검진을 받을 수 있나요?', '임신 중이거나 임신 가능성이 있는 경우, 태아에게 영향을 줄 수 있는 방사선 검사(X-ray, CT, 유방촬영 등)와 수면내시경, 부인과 검사 등은 불가합니다. 예약 및 검사 당일 반드시 의료진에게 임신 사실을 알려주셔야 합니다.', now() - INTERVAL '3 days'),
(3, '건강검진', '검진 당일 뇌 MRI나 초음파 등 추가 검사가 가능한가요?', '원칙적으로 당일 추가 검사는 영상의학과 및 해당 검사실의 당일 스케줄에 여유가 있을 때만 가능합니다. 대기 시간이 길어지거나 검사를 받지 못하실 수 있으므로 가급적 예약 단계에서 미리 추가 신청을 해 주시는 것이 좋습니다.', now() - INTERVAL '2 days'),
(2, '건강검진', '직장인(기업) 단체 건강검진 예약도 진행하나요?', '네, 본원 검진센터는 다양한 기업 및 기관과 협약을 맺고 맞춤형 직장인 단체 검진 프로그램을 운영하고 있습니다. 기업검진 전담 부서로 문의해 주시면 임직원 일괄 예약 등록 및 예산에 맞춘 프로그램 설계를 도와드립니다.', now() - INTERVAL '1 day');

-- ==================================================

-- 공지사항 10개씩 5페이지까지 +1
INSERT INTO notice (admin_staff_id, top_fix, title, content, thumbnail_img, write_date, read_count) VALUES

-- 1. 독감 예방접종 안내
(1, TRUE, '2025-2026 절기 인플루엔자(독감) 국가예방접종 안내', '어르신, 임신부, 어린이를 대상으로 한 독감 무료 예방접종을 실시합니다. 대상자별 접종 기간이 다르오니 첨부된 일정표를 확인하시고, 신분증을 지참하여 내원해 주시기 바랍니다. 일반 유료 접종도 가능합니다.', '/images/notice/notice_01.jpg', now() - INTERVAL '150 days', 2451),
-- 2. 추석 연휴 휴진 안내
(2, FALSE, '추석 연휴 외래 진료 휴진 및 응급실 운영 안내', '추석 연휴 기간 동안 외래 진료는 휴진합니다. 단, 응급의료센터는 연휴 기간 내내 365일 24시간 정상 운영되오니 응급 환자 발생 시 이용에 착오 없으시길 바랍니다. 즐겁고 건강한 명절 보내시길 기원합니다.', '/images/notice/notice_02.jpg', now() - INTERVAL '145 days', 1890),
-- 3. 새 MRI 장비 도입
(3, FALSE, '최첨단 3.0T MRI 장비 도입 및 본격 가동 개시', '환자분들에게 더욱 정확하고 신속한 진단 서비스를 제공하기 위해 최신형 3.0T MRI 장비를 추가 도입하여 본격적인 가동을 시작했습니다. 기존 장비 대비 촬영 속도가 빠르고 고해상도 영상 획득이 가능하여 진단 정확도를 높일 수 있습니다.', '/images/notice/notice_03.jpg', now() - INTERVAL '138 days', 3102),
-- 4. 주차 요금 변경 안내
(1, FALSE, '병원 부설 주차장 주차 요금 체계 변경 안내 (10월 1일부터)', '보다 원활한 주차장 운영 및 내원객 편의 증진을 위해 부득이하게 주차 요금 체계를 조정하게 되었습니다. 외래 진료 환자의 무료 주차 시간은 기존과 동일하게 유지되나, 추가 요금 및 일반 방문 차량 요금이 일부 인상됩니다. 자세한 내용은 홈페이지 주차 안내란을 참고해 주세요.', '/images/notice/notice_04.jpg', now() - INTERVAL '130 days', 4520),
-- 5. 건강 강좌 (당뇨병)
(2, FALSE, '[무료 건강강좌] 내분비내과 전문의와 함께하는 ''당뇨병 이기기''', '서구화된 식습관으로 늘어나는 당뇨병, 제대로 알고 관리하면 두렵지 않습니다. 내분비내과 전문의가 알려주는 실질적인 혈당 관리법, 식단, 운동 요법에 대한 무료 건강강좌를 개최합니다. 관심 있는 환자 및 보호자분들의 많은 참여 바랍니다.', '/images/notice/notice_05.jpg', now() - INTERVAL '122 days', 876),
-- 6. 신규 의료진 영입 (심장내과)
(3, FALSE, '심장내과 명의 김준호 교수 신규 의료진 영입 안내', '심혈관 질환 분야의 권위자인 김준호 교수가 11월 1일부터 본원에서 진료를 시작합니다. 협심증, 심근경색, 심부전 등 전문적인 심장 질환 진료 역량이 한층 강화될 예정입니다. 진료 예약은 홈페이지 및 콜센터를 통해 가능합니다.', '/images/notice/notice_06.jpg', now() - INTERVAL '115 days', 2105),
-- 7. 시스템 점검 안내
(1, FALSE, '안정적인 서비스를 위한 홈페이지 및 예약 시스템 서버 점검 안내', '더욱 안정적인 온라인 서비스 제공을 위해 정기 서버 점검을 진행합니다. 점검 시간 동안 홈페이지 접속 및 온라인 예약/조회 서비스 이용이 일시 중단되오니 양해 부탁드립니다. (점검 일시: 11월 15일 새벽 02:00 ~ 05:00)', '/images/notice/notice_07.jpg', now() - INTERVAL '108 days', 543),
-- 8. 연말 건강검진 독려
(2, FALSE, '연말 국가건강검진 수검 독려 캠페인 (미수검자 확인 필수)', '올해가 가기 전에 국가건강검진을 꼭 받으세요. 연말에는 수검자가 몰려 대기 시간이 길어질 수 있으니, 아직 검진을 받지 않은 대상자분들은 서둘러 예약 후 방문하시기를 권장합니다. 본인의 검진 대상 여부는 국민건강보험공단 홈페이지에서 확인 가능합니다.', '/images/notice/notice_08.jpg', now() - INTERVAL '95 days', 3670),
-- 9. 병원 내 마스크 착용 안내
(3, FALSE, '[중요] 병원 내 마스크 착용 의무 유지 안내 (방역수칙 준수)', '정부의 방역 지침에 따라 의료기관 내부에서는 감염 취약 계층 보호를 위해 마스크 착용 의무가 여전히 유지되고 있습니다. 환자, 보호자, 방문객 모두 병원 출입 시 반드시 마스크를 올바르게 착용해 주시기 바랍니다.', '/images/notice/notice_09.jpg', now() - INTERVAL '88 days', 5120),
-- 10. 식당가 리뉴얼 오픈
(1, FALSE, '본관 지하 1층 전문 식당가 및 편의시설 리뉴얼 오픈', '내원객 여러분에게 더 쾌적하고 다양한 편의를 제공하기 위해 진행되었던 본관 지하 1층 식당가 리뉴얼 공사가 완료되었습니다. 새롭게 단장한 푸드코트와 카페, 편의점을 많이 이용해 주시기 바랍니다.', '/images/notice/notice_10.jpg', now() - INTERVAL '80 days', 1230),
-- 11. 자원봉사자 모집
(2, TRUE, '따뜻한 동행, 2026년도 상반기 병원 자원봉사자 모집', '환자 안내, 이동 보조, 행정 지원 등 병원 곳곳에서 따뜻한 손길을 나누어 주실 2024년 상반기 자원봉사자를 모집합니다. 봉사활동에 관심과 열정이 있는 성인이라면 누구나 지원 가능합니다. 상세 내용은 첨부파일을 확인해주세요.', '/images/notice/notice_11.jpg', now() - INTERVAL '72 days', 450),
-- 12. 소아과 확장 이전
(3, FALSE, '소아청소년과 외래 확장 이전 및 진료 환경 개선 안내 (신관 2층)', '어린이 환자들이 더욱 넓고 쾌적한 환경에서 안심하고 진료받을 수 있도록 소아청소년과 외래를 신관 2층으로 확장 이전했습니다. 아이들의 눈높이에 맞춘 대기 공간과 수유실 등을 새롭게 마련했습니다.', '/images/notice/notice_12.jpg', now() - INTERVAL '65 days', 2340),
-- 13. 헌혈 캠페인 결과
(1, FALSE, '생명 나눔 사랑의 헌혈 캠페인 실시 결과 안내', '지난주 혈액 수급 안정화를 위해 진행된 ''사랑의 헌혈 캠페인''에 많은 임직원과 내원객분들이 동참해 주셨습니다. 따뜻한 생명 나눔에 참여해 주신 모든 분께 깊이 감사드립니다. 기부해주신 헌혈증은 어려운 환자들을 위해 소중히 사용하겠습니다.', '/images/notice/notice_13.jpg', now() - INTERVAL '58 days', 980),
-- 14. 의무기록 사본 발급 절차 안내
(2, FALSE, '환자 개인정보 보호를 위한 의무기록 사본 발급 절차 안내 강화', '의료법 및 개인정보보호법 강화에 따라 의무기록 사본 발급 시 본인 확인 절차가 엄격하게 적용됩니다. 환자 본인 방문 시 신분증 지참은 필수이며, 대리인 방문 시에는 환자의 자필 서명이 들어간 동의서와 위임장 등 구비 서류를 반드시 사전에 확인하시기 바랍니다.', '/images/notice/notice_14.jpg', now() - INTERVAL '50 days', 2890),
-- 15. 환자경험평가 1등급 획득
(3, FALSE, '본원, 건강보험심사평가원 주관 ''환자경험평가'' 1등급 획득', '환자분들이 직접 입원 경험을 평가하는 건강보험심사평가원 주관 ''환자경험평가''에서 본원이 우수한 성적으로 1등급을 획득했습니다. 앞으로도 환자 중심의 의료 서비스를 제공하고 더 나은 병원 환경을 만들기 위해 최선을 다하겠습니다.', '/images/notice/notice_15.jpg', now() - INTERVAL '42 days', 1560),
-- 16. 한파 대비 건강 수칙
(1, FALSE, '겨울철 한파 대비 한랭질환 예방 건강 수칙 안내', '강력한 한파가 예상됨에 따라 저체온증, 동상 등 한랭질환 예방에 주의가 필요합니다. 특히 만성질환자나 노약자는 체온 유지에 각별히 신경 써 주시고, 외출 시 방한용품을 꼭 착용하시기 바랍니다. 첨부된 건강 수칙을 확인해주세요.', '/images/notice/notice_16.jpg', now() - INTERVAL '35 days', 1120),
-- 17. 설 연휴 진료 안내
(2, TRUE, '2026년 설 연휴 기간 외래 진료 및 응급의료센터 운영 안내', '다가오는 설 연휴 기간(2/9 ~ 2/12) 외래 진료 일정을 안내해 드립니다. 연휴 기간 중 대체공휴일인 12일(월)은 오전 정상 진료를 실시하며, 응급의료센터는 연휴 내내 24시간 운영합니다. 새해 복 많이 받으십시오.', '/images/notice/notice_17.jpg', now() - INTERVAL '28 days', 3200),
-- 18. 셔틀버스 노선 변경
(3, FALSE, '병원 셔틀버스 운행 노선 및 시간표 일부 변경 알림', '지하철역 출구 공사로 인해 부득이하게 병원 셔틀버스 운행 노선과 정차 위치가 일부 변경되었습니다. 이용객 여러분의 착오 없으시길 바라며, 변경된 시간표와 노선도는 홈페이지 [오시는 길] 메뉴에서 확인 가능합니다.', '/images/notice/notice_18.jpg', now() - INTERVAL '21 days', 1450),
-- 19. 고객 만족도 조사
(1, FALSE, '더 나은 의료 서비스를 위한 2024년 상반기 고객 만족도 조사 실시', '병원을 이용하시는 고객님들의 소중한 의견을 듣고자 만족도 조사를 실시합니다. 여러분의 솔직한 의견은 서비스 개선을 위한 소중한 자료로 활용됩니다. 설문에 참여해주신 분들께는 추첨을 통해 소정의 모바일 쿠폰을 드립니다.', '/images/notice/notice_19.jpg', now() - INTERVAL '14 days', 760),
-- 20. 연말정산 서류 안내
(2, FALSE, '연말정산용 의료비 납입 확인서 발급 방법 안내 (홈택스 연동)', '연말정산 시즌을 맞아 의료비 납입 확인서 발급 문의가 많습니다. 본원 진료 내역은 국세청 연말정산 간소화 서비스에 자동 연동되므로 별도 서류 제출이 필요 없는 경우가 많습니다. 누락분이 있을 경우 홈페이지 온라인 제증명 메뉴에서 무료로 즉시 출력 가능합니다.', '/images/notice/notice_20.jpg', now() - INTERVAL '124 days', 4890),
-- 21. 휠체어 대여소 위치 변경
(3, FALSE, '본관 1층 휠체어 무료 대여소 위치 변경 안내', '고객 안내 데스크 리모델링 공사로 인해 휠체어 및 유모차 대여소 위치가 본관 1층 서문 입구 쪽으로 이동되었습니다. 이용에 참고 부탁드립니다.', '/images/notice/notice_21.jpg', now() - INTERVAL '120 days', 1520),
-- 22. 신규 의료진 영입 (비뇨의학과)
(1, FALSE, '비뇨의학과 로봇수술 전문가 박현우 교수 진료 개시', '전립선암 및 신장암 로봇수술의 권위자인 박현우 교수가 3월부터 본원 비뇨의학과에서 진료를 시작합니다. 환자분들께 더욱 안전하고 정밀한 수술을 제공하겠습니다.', '/images/notice/notice_22.jpg', now() - INTERVAL '116 days', 2011),
-- 23. 외래 주차장 일부 통제
(2, FALSE, '외래 2주차장 바닥 보수 공사로 인한 일부 통제 안내', '외래 2주차장 바닥 에폭시 보수 공사로 인하여 이번 주말 동안 해당 구역의 주차가 전면 통제됩니다. 방문객들께서는 1주차장 및 지하 주차장을 이용해 주시기 바랍니다.', '/images/notice/notice_23.jpg', now() - INTERVAL '112 days', 3421),
-- 24. 키오스크 추가 설치
(3, FALSE, '대기시간 단축을 위한 무인 수납기(키오스크) 추가 설치', '진료 후 수납 대기시간을 줄이기 위해 각 층 외래 진료 구역에 무인 수납기(키오스크) 10대를 추가 설치하였습니다. 신용카드로 간편하게 수납 및 처방전 발급이 가능합니다.', '/images/notice/notice_24.jpg', now() - INTERVAL '108 days', 1895),
-- 25. 호흡기환자 진료센터 운영
(1, FALSE, '원스톱 호흡기환자 진료센터 지속 운영 안내', '코로나19 등 호흡기 감염병의 신속한 진단과 치료를 위해 원스톱 호흡기환자 진료센터를 본관 외부 독립 건물에서 지속 운영합니다. 발열 및 기침 증상이 있으신 분들은 해당 센터를 먼저 방문해주세요.', '/images/notice/notice_25.jpg', now() - INTERVAL '104 days', 4122),
-- 26. 영양 교실 (당뇨병)
(2, FALSE, '[식단관리] 당뇨병 환자를 위한 영양 교실 참가자 모집', '식단 관리가 필수적인 당뇨병 환자와 보호자를 위해 임상영양사가 직접 지도하는 무료 영양 교실을 엽니다. 실제 식단 모형을 보며 배우는 유익한 시간에 많은 참여 바랍니다.', '/images/notice/notice_26.jpg', now() - INTERVAL '100 days', 780),
-- 27. 대표번호 변경 안내
(3, FALSE, '병원 통합 콜센터 대표번호 변경 안내 (1588-XXXX)', '고객 상담의 편의성과 통화 품질 개선을 위해 병원 통합 콜센터 대표번호가 새롭게 변경되었습니다. 기존 번호로 전화하셔도 당분간 자동 연결되오니 참고하시기 바랍니다.', '/images/notice/notice_27.jpg', now() - INTERVAL '96 days', 5630),
-- 28. 근로자의 날 휴진 안내
(1, TRUE, '5월 1일 근로자의 날 외래 진료 휴진 안내', '5월 1일(근로자의 날)은 본원 전체 외래 진료가 휴진합니다. 단, 응급의료센터와 중환자실은 정상 운영됩니다. 병원 이용에 불편 없으시길 바랍니다.', '/images/notice/notice_28.jpg', now() - INTERVAL '92 days', 2314),
-- 29. 응급의료센터 리모델링
(2, FALSE, '응급의료센터 리모델링 공사 및 임시 출입구 안내', '응급환자 수용 능력 확대 및 감염 관리 강화를 위해 응급의료센터 내부 리모델링 공사를 진행합니다. 공사 기간 동안 출입구가 우측 임시 출입구로 변경되오니 안내판을 참조해 주십시오.', '/images/notice/notice_29.jpg', now() - INTERVAL '88 days', 3110),
-- 30. 제증명 무인발급기
(3, FALSE, '야간/주말 제증명 무인발급기 24시간 운영 안내', '원무과 창구 운영 시간이 종료된 야간 및 주말에도 필수 서류를 발급받으실 수 있도록 본관 1층 무인발급기를 24시간 운영합니다. 발급 가능 서류 목록은 홈페이지를 참조하세요.', '/images/notice/notice_30.jpg', now() - INTERVAL '84 days', 4021),
-- 31. 장례식장 새단장
(1, FALSE, '병원 직영 장례식장 전면 리모델링 새단장 오픈', '유가족분들이 보다 편안하고 경건한 분위기에서 장례를 모실 수 있도록 장례식장 접객실 및 빈소를 전면 리모델링하여 새롭게 오픈했습니다.', '/images/notice/notice_31.jpg', now() - INTERVAL '80 days', 940),
-- 32. 간호·간병통합 병동 확대
(2, FALSE, '보호자 없는 안심 병실, 간호·간병통합서비스 병동 확대 운영', '보호자의 간병 부담을 줄이고 환자 안전을 높이기 위해 간호·간병통합서비스 병동을 기존 2개 병동에서 4개 병동으로 대폭 확대 운영합니다.', '/images/notice/notice_32.jpg', now() - INTERVAL '76 days', 3850),
-- 33. 치매 예방 건강강좌
(3, FALSE, '[건강강좌] 신경과 전문의가 알려주는 ''치매 조기 발견과 예방''', '고령화 시대의 불청객, 치매의 조기 진단법과 일상생활 속 예방 수칙에 대해 신경과 전문의가 알기 쉽게 설명해 드립니다. 참석자 전원에게 소정의 기념품을 드립니다.', '/images/notice/notice_33.jpg', now() - INTERVAL '72 days', 1150),
-- 34. 어버이날 카네이션 행사
(1, FALSE, '어버이날 맞이 입원 환자 대상 카네이션 증정 행사 안내', '5월 8일 어버이날을 맞아 병동에 입원 중이신 65세 이상 어르신 환자분들께 의료진이 직접 카네이션을 달아드리고 빠른 쾌유를 기원하는 행사를 진행합니다.', '/images/notice/notice_34.jpg', now() - INTERVAL '68 days', 670),
-- 35. 소화기내과 위치 변경
(2, FALSE, '소화기내과 및 내시경센터 본관 3층으로 통합 이전 안내', '분리되어 있던 소화기내과 외래와 내시경센터를 본관 3층으로 통합 이전하여 원스톱 진료 시스템을 구축했습니다. 동선이 짧아져 더욱 편리하게 진료받으실 수 있습니다.', '/images/notice/notice_35.jpg', now() - INTERVAL '64 days', 2145),
-- 36. 무더위 쉼터 운영
(3, FALSE, '하절기 폭염 대비 지역주민을 위한 ''무더위 쉼터'' 개방', '올여름 기록적인 폭염이 예상됨에 따라, 지역주민 누구나 더위를 피하고 시원한 생수를 드실 수 있도록 신관 1층 로비 일부를 무더위 쉼터로 개방합니다.', '/images/notice/notice_36.jpg', now() - INTERVAL '60 days', 890),
-- 37. 입원환자 식단 개편
(1, FALSE, '입원환자 만족도 향상을 위한 맞춤형 선택 식단 전면 개편', '환자분들의 빠른 회복과 입맛을 돋우기 위해 영양팀에서 입원환자 식단을 전면 개편했습니다. 매일 제공되는 2가지 메뉴 중 원하시는 식단을 태블릿으로 직접 선택하실 수 있습니다.', '/images/notice/notice_37.jpg', now() - INTERVAL '56 days', 3320),
-- 38. 수면다원검사실 확장
(2, FALSE, '코골이·수면무호흡증 진단, 수면다원검사실 확장 오픈', '수면 장애를 겪는 환자분들이 늘어남에 따라 이비인후과 수면다원검사실을 2개 병상으로 확장 오픈했습니다. 이제 대기 기간을 대폭 줄여 빠른 검사가 가능합니다.', '/images/notice/notice_38.jpg', now() - INTERVAL '52 days', 1780),
-- 39. 모바일 앱 업데이트
(3, FALSE, '스마트 병원 모바일 앱 V2.0 업데이트 안내 (결제 기능 추가)', '진료카드 기능만 제공하던 기존 모바일 앱을 대폭 업그레이드했습니다. 이제 모바일 앱을 통해 진료비 결제, 대기 순번 확인, 실손보험 청구까지 한 번에 가능합니다.', '/images/notice/notice_39.jpg', now() - INTERVAL '48 days', 4510),
-- 40. 개원 20주년 음악회
(1, FALSE, '개원 20주년 기념 환자와 함께하는 로비 힐링 음악회 개최', '본원 개원 20주년을 맞이하여, 투병 생활에 지친 환자와 보호자들을 위로하기 위해 지역 교향악단을 초청하여 다채로운 클래식 음악회를 개최합니다.', '/images/notice/notice_40.jpg', now() - INTERVAL '44 days', 1050),
-- 41. 외국인 통역 확대
(2, FALSE, '국제진료센터 외국인 환자 전담 통역 서비스(러시아어/몽골어) 추가', '최근 증가하는 다국적 환자들의 원활한 진료를 위해 기존 영어, 중국어 통역 외에 러시아어 및 몽골어 전담 코디네이터를 추가로 배치했습니다.', '/images/notice/notice_41.jpg', now() - INTERVAL '40 days', 620),
-- 42. 건강검진 카카오 챗봇
(3, FALSE, '건강검진센터 카카오톡 챗봇 예약 및 문진 시스템 도입', '건강검진 예약부터 사전 전자 문진표 작성까지 카카오톡 챗봇으로 24시간 쉽고 빠르게 진행할 수 있는 스마트 검진 시스템을 도입했습니다.', '/images/notice/notice_42.jpg', now() - INTERVAL '36 days', 2890),
-- 43. 신생아중환자실 면회
(1, FALSE, '신생아중환자실(NICU) 감염 예방을 위한 면회 제한 재안내', '면역력이 취약한 미숙아 및 신생아들을 보호하기 위해 신생아중환자실의 직계가족 면회를 주 1회로 제한합니다. 지정된 시간 외에는 유리창 면회만 가능하오니 양해 부탁드립니다.', '/images/notice/notice_43.jpg', now() - INTERVAL '32 days', 1450),
-- 44. 로봇수술 1000례 달성
(2, FALSE, '본원 로봇수술센터, 다빈치 로봇수술 누적 1,000례 성공적 달성', '외과, 비뇨의학과, 산부인과 의료진의 협진을 바탕으로 본원 로봇수술센터가 단기간 내 최소침습 로봇수술 누적 1,000례를 달성했습니다. 앞으로도 환자들의 빠른 회복을 위해 노력하겠습니다.', '/images/notice/notice_44.jpg', now() - INTERVAL '28 days', 3760),
-- 45. 무료 Wi-Fi 개선
(3, FALSE, '환자 편의 증진을 위한 전 병동 무료 Wi-Fi 속도 개선 및 중계기 증설', '입원 환자분들의 스마트 기기 이용 편의를 위해 전 병동의 무선 공유기를 최신 Wi-Fi 6 장비로 교체하고 음영 지역에 중계기를 추가 설치했습니다. 쾌적하게 이용하시기 바랍니다.', '/images/notice/notice_45.jpg', now() - INTERVAL '24 days', 4920),
-- 46. 감염관리 우수부서
(1, TRUE, '2026년도 1분기 손 위생 및 감염관리 우수부서 표창 소식', '환자 감염 예방의 기본인 ''손 위생 수행률'' 모니터링 결과, 내과계 중환자실과 71병동이 1분기 최우수 부서로 선정되어 병원장 표창을 받았습니다.', '/images/notice/notice_46.jpg', now() - INTERVAL '20 days', 810),
-- 47. 정형외과 스포츠클리닉
(2, FALSE, '정형외과 스포츠의학 전문 클리닉 신규 개설 안내', '운동 중 발생하는 십자인대 파열, 어깨 회전근개 손상 등 스포츠 손상을 전문적으로 수술하고 체계적으로 재활을 돕는 스포츠의학 클리닉을 새롭게 개설했습니다.', '/images/notice/notice_47.jpg', now() - INTERVAL '16 days', 1650),
-- 48. 개인정보처리방침 개정
(3, FALSE, '홈페이지 및 진료 정보 관련 개인정보처리방침 개정 사전 안내', '관련 법령 개정에 따라 본원 고객의 개인정보 수집 및 이용, 제3자 제공에 대한 개인정보처리방침이 일부 변경됩니다. 상세 개정 내용은 첨부된 신구대조표를 확인해 주시기 바랍니다.', '/images/notice/notice_48.jpg', now() - INTERVAL '12 days', 410),
-- 49. 산타클로스 행사
(1, FALSE, '크리스마스 맞이 소아청소년과 병동 산타클로스 깜짝 방문 행사', '다가오는 크리스마스를 병원에서 보내야 하는 환아들을 위해 의료진들이 산타클로스와 루돌프로 변신하여 소아병동에 선물을 전달하는 뜻깊은 행사를 가졌습니다.', '/images/notice/notice_49.jpg', now() - INTERVAL '8 days', 3120),
-- 50. 환자 안전의 날
(2, FALSE, '''정확한 환자 확인'' 2024년 환자 안전의 날 캠페인 실시', '투약 및 검사 전 발생할 수 있는 오류를 원천 차단하기 위해 임직원과 환자가 함께 참여하는 ''정확한 환자 확인(이름, 생년월일) 캠페인''을 1층 로비에서 진행했습니다.', '/images/notice/notice_50.jpg', now() - INTERVAL '4 days', 2180),
-- 51. 임시공휴일 진료 안내
(3, FALSE, '10월 1일 국군의 날(임시공휴일) 오전 외래 진료 정상 운영 안내', '정부에서 지정한 10월 1일 임시공휴일에 환자분들의 진료 불편을 최소화하고자 오전 9시부터 13시까지 외래 진료를 정상 운영합니다. 사전 예약 시 착오 없으시길 바랍니다.', '/images/notice/notice_51.jpg', now() - INTERVAL '2 days', 5030);

-- ==================================================

-- 건강이야기 9개씩 5페이지까지 +1
INSERT INTO health_story (admin_staff_id, title, content, thumbnail_img, write_date, read_count) VALUES
-- 1. 침묵의 살인자, 고혈압
(1, '침묵의 살인자 고혈압, 평소 혈압 관리가 중요한 이유', '고혈압은 초기 증상이 거의 없어 ''침묵의 살인자''로 불립니다. 방치할 경우 뇌졸중, 심근경색 등 치명적인 합병증을 유발할 수 있습니다. 올바른 혈압 측정법과 나트륨 섭취를 줄이는 식습관 개선에 대해 알아봅니다.', '/images/hs/hs_01.jpg', now() - INTERVAL '200 days', 3421),
-- 2. 당뇨병 식단
(2, '달콤한 유혹을 이겨라! 당뇨병 예방을 위한 건강한 식단 가이드', '당뇨병은 식단 관리가 치료의 절반을 차지할 정도로 중요합니다. 혈당 스파이크를 막는 식사 순서(채소-단백질-탄수화물)와 단순당이 적은 착한 탄수화물 고르는 법을 내분비내과 전문의가 소개합니다.', '/images/hs/hs_02.jpg', now() - INTERVAL '195 days', 5120),
-- 3. 거북목 증후군
(3, '스마트폰이 만든 불청객, 거북목 증후군 10분 스트레칭으로 극복하기', '컴퓨터와 스마트폰 사용 시간이 늘면서 현대인들의 목 건강에 적신호가 켜졌습니다. 뒷목의 뻐근함과 두통을 유발하는 거북목 증후군을 예방하고, 사무실에서도 쉽게 따라 할 수 있는 10분 교정 스트레칭을 알려드립니다.', '/images/hs/hs_03.jpg', now() - INTERVAL '191 days', 8930),
-- 4. 수면의 중요성
(1, '잠이 보약이다! 불면증을 이겨내고 꿀잠 자는 5가지 수면 습관', '수면 부족은 만성 피로뿐만 아니라 면역력 저하, 비만, 우울증의 원인이 됩니다. 취침 전 스마트폰 사용 줄이기, 일정한 기상 시간 유지하기 등 수면의 질을 높이는 올바른 수면 위생(Sleep Hygiene) 수칙을 알아봅니다.', '/images/hs/hs_04.jpg', now() - INTERVAL '187 days', 4502),
-- 5. 봄철 알레르기 비염
(2, '봄바람과 함께 찾아오는 불청객, 알레르기 비염 대처법', '꽃가루와 황사가 심한 봄철이면 콧물과 재채기로 고통받는 알레르기 비염 환자가 급증합니다. 코 세척 방법, 외출 시 주의사항, 그리고 면역 요법 등 비염을 현명하게 관리하고 예방하는 팁을 소개합니다.', '/images/hs/hs_05.jpg', now() - INTERVAL '183 days', 2150),
-- 6. 여름철 온열질환
(3, '무더운 여름철, 일사병과 열사병의 차이점과 응급처치법', '폭염이 지속되는 여름에는 온열질환에 각별히 주의해야 합니다. 땀을 많이 흘리는 일사병과 체온 조절 기능이 망가져 생명이 위험해지는 열사병의 차이를 알아보고, 증상 발생 시 올바른 응급처치 방법을 숙지해 둡시다.', '/images/hs/hs_06.jpg', now() - INTERVAL '179 days', 3820),
-- 7. 가을철 쯔가무시병
(1, '가을철 야외활동 주의보, 털진드기가 옮기는 쯔가무시병 예방수칙', '단풍놀이와 벌초 등 야외활동이 잦은 가을에는 진드기 매개 감염병인 쯔가무시병을 조심해야 합니다. 야외활동 시 긴 소매 옷 착용, 기피제 사용 등 진드기에 물리지 않기 위한 필수 예방 수칙을 알아봅니다.', '/images/hs/hs_07.jpg', now() - INTERVAL '175 days', 1560),
-- 8. 겨울철 심혈관 질환
(2, '추운 겨울 아침, 돌연사를 부르는 심혈관 질환 주의보', '기온이 뚝 떨어지는 겨울철 아침에는 혈관이 수축하여 혈압이 급격히 상승하므로 심근경색이나 뇌졸중의 위험이 크게 높아집니다. 겨울철 이른 아침 운동을 피하고 체온을 유지하는 외출 전 방한 수칙을 짚어봅니다.', '/images/hs/hs_08.jpg', now() - INTERVAL '171 days', 4210),
-- 9. 우울증과 스트레스
(3, '마음의 감기 ''우울증'', 숨기지 말고 치료받아야 하는 이유', '우울증은 누구나 겪을 수 있는 마음의 감기지만, 방치하면 심각한 결과를 초래할 수 있습니다. 일상생활의 흥미 저하, 수면 장애 등 우울증의 초기 증상을 알아보고, 정신건강의학과 상담의 중요성에 대해 이야기합니다.', '/images/hs/hs_09.jpg', now() - INTERVAL '167 days', 2890),
-- 10. 카페인 섭취
(1, '모닝커피 한 잔의 두 얼굴, 건강한 카페인 섭취 가이드', '적당한 카페인은 피로를 줄이고 집중력을 높여주지만, 과다 섭취 시 두근거림, 불면증, 위장 장애를 유발할 수 있습니다. 성인 기준 하루 권장 카페인 섭취량과 커피 대신 마시기 좋은 건강한 차를 추천해 드립니다.', '/images/hs/hs_10.jpg', now() - INTERVAL '163 days', 5621),
-- 11. 안구건조증
(2, '뻑뻑하고 시린 눈, 현대인의 고질병 안구건조증 예방 팁', '모니터와 스마트폰을 장시간 들여다보는 현대인들에게 안구건조증은 매우 흔한 질환입니다. 인공눈물의 올바른 사용법과 눈의 피로를 풀어주는 온찜질, 그리고 50분 작업 후 10분 휴식하는 눈 건강 루틴을 소개합니다.', '/images/hs/hs_11.jpg', now() - INTERVAL '159 days', 7100),
-- 12. 백내장과 녹내장
(3, '나이 들면 침침해지는 눈, 백내장과 녹내장은 어떻게 다를까?', '노년기 대표적인 안과 질환인 백내장과 녹내장. 수정체가 혼탁해지는 백내장과 시신경이 손상되는 녹내장의 원인, 주요 증상, 그리고 조기 발견을 위한 정기적인 안과 검진의 중요성을 비교해 봅니다.', '/images/hs/hs_12.jpg', now() - INTERVAL '155 days', 3120),
-- 13. 골다공증 예방
(1, '구멍 뚫린 뼈, 소리 없는 뼈 도둑 ''골다공증'' 예방과 관리', '골다공증은 뼈의 양이 감소하고 강도가 약해져 작은 충격에도 쉽게 골절이 발생하게 만드는 질환입니다. 칼슘과 비타민 D가 풍부한 음식 섭취와 체중 부하 운동 등 뼈를 튼튼하게 유지하는 생활 습관을 알아봅니다.', '/images/hs/hs_13.jpg', now() - INTERVAL '151 days', 2450),
-- 14. 오십견
(2, '어깨가 얼어붙은 듯한 통증, 오십견(동결견)의 오해와 진실', '50대 전후로 흔히 나타난다고 하여 이름 붙여진 오십견. 팔을 위로 올리기 힘든 통증이 특징입니다. 자연히 낫는다고 방치하기 쉬운 오십견의 정확한 원인과 스트레칭, 주사 치료 등 적극적인 관리법을 소개합니다.', '/images/hs/hs_14.jpg', now() - INTERVAL '146 days', 3890),
-- 15. 치매 예방
(3, '두뇌를 젊게 유지하는 습관, 일상 속 치매 예방 가이드', '나이가 들면서 가장 두려워하는 질환 중 하나가 바로 치매입니다. 규칙적인 운동, 활발한 두뇌 활동(독서, 퍼즐 등), 사회적 교류, 그리고 심혈관 질환 관리를 통해 치매 발병 위험을 낮추는 방법을 알려드립니다.', '/images/hs/hs_15.jpg', now() - INTERVAL '142 days', 5010),
-- 16. 뇌졸중 전조증상
(1, '골든타임이 생명을 살린다! 꼭 알아둬야 할 뇌졸중 전조증상 FAST', '뇌졸중은 발생 직후 빠른 조치가 생명과 후유증을 좌우합니다. 얼굴 마비(Face), 팔 다리 힘 빠짐(Arms), 언어 장애(Speech) 등 뇌졸중을 의심해야 하는 전조증상과 즉시 응급실로 가야 하는 시간(Time)의 중요성을 강조합니다.', '/images/hs/hs_16.jpg', now() - INTERVAL '138 days', 6120),
-- 17. 대상포진
(2, '출산의 고통과 맞먹는 대상포진, 골든타임 72시간 내 치료 필수', '어릴 적 수두 바이러스가 신경절에 숨어 있다가 면역력이 떨어지면 띠 모양의 수포와 극심한 통증을 유발하는 대상포진. 발진 발생 후 72시간 내에 항바이러스제를 투여해야 신경통 후유증을 막을 수 있습니다.', '/images/hs/hs_17.jpg', now() - INTERVAL '134 days', 4320),
-- 18. 올바른 양치질
(3, '당신의 양치질은 안녕하십니까? 치과의사가 알려주는 3-3-3 법칙', '치아 건강의 기본은 올바른 칫솔질입니다. 하루 3번, 식후 3분 이내, 3분 동안 닦는 3-3-3 법칙은 물론, 치간 칫솔과 치실 사용의 중요성, 정기적인 스케일링으로 잇몸 질환을 예방하는 방법을 알아봅니다.', '/images/hs/hs_18.jpg', now() - INTERVAL '130 days', 8210),
-- 19. 임산부 영양
(1, '엄마와 아기를 위한 건강한 열 달, 임신 주기별 필수 영양소 가이드', '임신 초기에는 엽산, 중기에는 철분, 후기에는 칼슘 등 임신 주기에 따라 태아 발달과 산모 건강에 꼭 필요한 영양소가 다릅니다. 산부인과 전문의가 권장하는 주수별 필수 영양제와 식단 관리법을 정리했습니다.', '/images/hs/hs_19.jpg', now() - INTERVAL '126 days', 1890),
-- 20. 소아 비만
(2, '어릴 때 찐 살은 키로 간다? 소아 비만의 위험성과 부모의 역할', '소아 비만은 세포의 수 자체가 늘어나 성인 비만으로 이어질 확률이 매우 높으며, 성조숙증을 유발해 성장에 악영향을 미칩니다. 아이의 올바른 식습관 형성과 규칙적인 운동을 위한 가족 전체의 노력이 필요합니다.', '/images/hs/hs_20.jpg', now() - INTERVAL '122 days', 2760),
-- 21. 갱년기 극복
(3, '제2의 사춘기 ''여성 갱년기'', 우울감과 안면홍조 슬기롭게 극복하기', '폐경 전후로 여성호르몬이 급감하며 찾아오는 갱년기. 안면홍조, 발한, 수면장애, 감정 기복 등 다양한 증상으로 고통받는 여성들을 위해 호르몬 대체 요법과 갱년기 극복에 도움이 되는 식생활 습관을 소개합니다.', '/images/hs/hs_21.jpg', now() - INTERVAL '118 days', 3540),
-- 22. 전립선 비대증
(1, '밤마다 화장실 들락날락... 중년 남성의 말 못 할 고민 ''전립선 비대증''', '50대 이상 남성의 절반이 겪는다는 전립선 비대증. 소변 줄기가 약해지고 잔뇨감이 남는다면 의심해봐야 합니다. 전립선 건강을 지키는 생활 수칙과 약물 및 수술적 치료 방법에 대해 비뇨의학과 전문의가 알려드립니다.', '/images/hs/hs_22.jpg', now() - INTERVAL '114 days', 2110),
-- 23. 위염과 식습관
(2, '속 쓰림과 소화불량, 한국인의 국민병 ''만성 위염'' 관리하기', '맵고 짠 음식, 잦은 회식과 스트레스로 한국인의 위는 늘 피곤합니다. 만성 위염을 방치하면 위궤양이나 위암으로 발전할 수 있습니다. 위 점막을 보호하는 양배추 섭취, 야식 금지 등 위 건강을 지키는 식습관을 알아봅니다.', '/images/hs/hs_23.jpg', now() - INTERVAL '110 days', 4650),
-- 24. 대장용종
(3, '대장암의 씨앗 ''대장용종'', 대장내시경으로 발견 즉시 제거해야', '대장용종은 대장 점막이 비정상적으로 자라 혹처럼 돌출된 것으로, 이 중 선종성 용종은 방치하면 대장암으로 진행됩니다. 50세 이상이라면 증상이 없어도 정기적인 대장내시경 검사를 받아야 하는 이유를 설명합니다.', '/images/hs/hs_24.jpg', now() - INTERVAL '106 days', 3980),
-- 25. 지방간
(1, '술 안 마셔도 걸린다? 탄수화물이 주범인 ''비알콜성 지방간''', '지방간은 잦은 음주뿐만 아니라 과도한 탄수화물과 당분 섭취로 인한 비알콜성 지방간 환자 비율이 급증하고 있습니다. 간경변증으로 악화되기 전, 체중 감량과 식단 조절을 통해 간 건강을 되돌리는 방법을 소개합니다.', '/images/hs/hs_25.jpg', now() - INTERVAL '102 days', 5230),
-- 26. 통풍
(2, '바람만 스쳐도 아프다는 ''통풍'', 맥주와 치킨이 위험한 이유', '혈중 요산 농도가 높아져 관절에 쌓이면서 극심한 통증을 유발하는 통풍. 퓨린이 많이 함유된 맥주와 육류 위주의 식습관이 주요 원인입니다. 통풍 발작을 예방하기 위한 식이요법과 충분한 수분 섭취의 중요성을 알아봅니다.', '/images/hs/hs_26.jpg', now() - INTERVAL '98 days', 3150),
-- 27. 류마티스 관절염
(3, '아침마다 관절이 뻣뻣하다면? 퇴행성 관절염과 류마티스 관절염의 차이', '관절염이라고 다 같은 관절염이 아닙니다. 노화로 인한 연골 손상인 퇴행성 관절염과 면역 체계 이상으로 발생하는 류마티스 관절염은 원인과 치료법이 완전히 다릅니다. 증상의 차이와 조기 진단의 중요성을 비교해 드립니다.', '/images/hs/hs_27.jpg', now() - INTERVAL '94 days', 2870),
-- 28. 갑상선 질환
(1, '이유 없이 피곤하거나 살이 찐다면? 갑상선 기능 이상 의심해봐야', '목 앞쪽에 위치한 나비 모양의 내분비기관 갑상선. 호르몬이 과다 분비되는 항진증은 체중 감소와 두근거림을, 부족한 저하증은 극심한 피로와 체중 증가를 유발합니다. 갑상선 질환의 주요 증상과 혈액 검사에 대해 알아봅니다.', '/images/hs/hs_28.jpg', now() - INTERVAL '90 days', 4120),
-- 29. 수족냉증
(2, '한여름에도 손발이 꽁꽁, 혈액순환 장애가 원인인 수족냉증 탈출법', '따뜻한 실내에서도 손발이 차가워 고통받는 수족냉증. 단순 체질 문제가 아닌 혈액순환 장애나 자율신경 이상일 수 있습니다. 반신욕, 족욕, 가벼운 유산소 운동 등 체온을 높이고 혈액순환을 돕는 생활 요법을 소개합니다.', '/images/hs/hs_29.jpg', now() - INTERVAL '86 days', 3450),
-- 30. 과민성 대장 증후군
(3, '긴장하면 배가 살살 아파요, 뇌와 장이 연결된 ''과민성 대장 증후군''', '중요한 시험이나 회의 직전, 갑자기 찾아오는 복통과 설사. 내시경 상으로는 이상이 없지만 스트레스에 예방하게 반응하는 과민성 대장 증후군의 원인과 장 건강에 좋은 포드맵(FODMAP) 식단 조절법을 알려드립니다.', '/images/hs/hs_30.jpg', now() - INTERVAL '82 days', 5120),
-- 31. 족저근막염
(1, '아침 첫 발을 내디딜 때 찌릿한 통증, 발바닥의 경고 ''족저근막염''', '발바닥 근육을 감싸는 막에 염증이 생기는 족저근막염. 불편한 신발 착용, 무리한 운동, 체중 증가가 주원인입니다. 통증을 완화하는 발바닥 마사지와 종아리 스트레칭, 그리고 푹신한 신발 선택의 중요성을 강조합니다.', '/images/hs/hs_31.jpg', now() - INTERVAL '78 days', 4320),
-- 32. 이명
(2, '귀에서 매미 소리가 들려요! 스트레스가 악화시키는 ''이명'' 대처법', '외부의 소리 자극이 없는데도 귀에서 소음이 들리는 이명. 난청을 동반하는 경우가 많으며 극심한 스트레스와 피로로 인해 악화됩니다. 이명의 원인을 파악하기 위한 정밀 청력 검사와 소리 치료(Tinnitus Retraining Therapy)를 알아봅니다.', '/images/hs/hs_32.jpg', now() - INTERVAL '74 days', 1780),
-- 33. 하지정맥류
(3, '다리에 울퉁불퉁 핏줄이? 오래 서서 일하는 사람을 위협하는 하지정맥류', '다리 정맥의 판막이 손상되어 피가 역류하고 혈관이 부풀어 오르는 하지정맥류. 다리가 무겁고 쉽게 붓는다면 의심해야 합니다. 꽉 끼는 옷을 피하고 의료용 압박스타킹을 착용하는 등 일상 속 다리 건강 관리법을 소개합니다.', '/images/hs/hs_33.jpg', now() - INTERVAL '70 days', 3650),
-- 34. 탈모 예방
(1, '머리카락이 우수수... 스트레스성 원형탈모부터 유전성 탈모까지', '탈모는 더 이상 중년 남성만의 고민이 아닙니다. 스트레스, 다이어트, 환경 오염으로 인해 2030 젊은 층의 탈모 환자가 급증하고 있습니다. 탈모의 유형별 원인을 알아보고, 두피 건강을 지키는 올바른 샴푸 습관을 배워봅니다.', '/images/hs/hs_34.jpg', now() - INTERVAL '66 days', 8120),
-- 35. 자외선 차단
(2, '피부 노화와 피부암의 주범, 365일 실천해야 하는 올바른 자외선 차단법', '자외선은 피부 탄력을 떨어뜨려 주름을 만들고 심하면 피부암을 유발합니다. 흐린 날이나 실내에서도 자외선 A(UVA)는 피부 깊숙이 침투하므로, 외출 30분 전 자외선 차단제 바르기와 2시간마다 덧바르기의 중요성을 설명합니다.', '/images/hs/hs_35.jpg', now() - INTERVAL '62 days', 5030),
-- 36. 미세먼지 대처
(3, '호흡기를 위협하는 은밀한 살인자, 미세먼지 대처하는 올바른 자세', '초미세먼지는 호흡기에서 걸러지지 않고 혈관을 타고 온몸을 돌며 염증을 유발합니다. 미세먼지 농도가 높은 날의 올바른 KF 보건용 마스크 착용법과 외출 후 개인위생 철저, 실내 환기 요령에 대해 알아봅니다.', '/images/hs/hs_36.jpg', now() - INTERVAL '58 days', 4120),
-- 37. 올바른 걷기 자세
(1, '만병통치약 걷기 운동, 제대로 걷고 있나요? 올바른 보행 자세 체크!', '걷기는 부상 위험이 적고 전신 근육을 사용하는 최고의 유산소 운동입니다. 하지만 허리를 굽히거나 팔자걸음으로 걸으면 오히려 관절에 무리가 갑니다. 시선, 팔의 스윙, 발구름 등 운동 효과를 극대화하는 올바른 걷기 자세를 배워봅시다.', '/images/hs/hs_37.jpg', now() - INTERVAL '54 days', 6540),
-- 38. 스트레칭 효과
(2, '하루 10분 투자의 기적! 굳은 몸을 깨우는 아침저녁 스트레칭의 효과', '규칙적인 스트레칭은 근육의 긴장을 완화하고 혈액순환을 촉진하여 만성 피로를 줄여줍니다. 아침에는 밤새 굳어있던 관절을 풀어주는 가벼운 스트레칭을, 저녁에는 숙면을 돕는 이완 스트레칭을 실천하는 방법을 소개합니다.', '/images/hs/hs_38.jpg', now() - INTERVAL '50 days', 3210),
-- 39. 단백질 섭취량
(3, '근육 연금을 쌓아라! 노년기 건강을 좌우하는 올바른 단백질 섭취법', '나이가 들면 근육량이 자연스럽게 감소하는 근감소증이 발생합니다. 근육 손실을 막기 위해서는 매끼 체중 1kg당 1g~1.2g의 양질의 단백질 섭취가 필수적입니다. 소화가 잘되는 식물성 및 동물성 단백질 공급원을 알아봅니다.', '/images/hs/hs_39.jpg', now() - INTERVAL '46 days', 4780),
-- 40. 프로바이오틱스와 장 건강
(1, '면역력의 70%는 장에 있다! 장내 유익균을 늘리는 프로바이오틱스 섭취', '우리 몸의 면역 세포 대부분은 장에 분포합니다. 유해균을 억제하고 유익균을 늘려 장 건강을 지켜주는 유산균(프로바이오틱스)의 역할과, 유익균의 먹이가 되는 프리바이오틱스가 풍부한 식재료에 대해 알아봅니다.', '/images/hs/hs_40.jpg', now() - INTERVAL '42 days', 5630),
-- 41. 비타민 D 결핍
(2, '햇빛 비타민 D가 부족하면 벌어지는 일들 (우울증부터 면역력 저하까지)', '실내 생활이 긴 현대인의 90% 이상이 비타민 D 결핍 상태입니다. 비타민 D는 칼슘 흡수를 도와 뼈 건강을 지킬 뿐만 아니라 우울증 예방과 면역력 강화에 필수적입니다. 하루 20분 햇볕 쬐기와 영양제 섭취의 필요성을 강조합니다.', '/images/hs/hs_41.jpg', now() - INTERVAL '38 days', 6120),
-- 42. 스마트폰과 수면장애
(3, '자기 전 스마트폰이 뇌에 미치는 악영향 (블루라이트와 멜라토닌)', '어두운 방에서 스마트폰의 블루라이트에 노출되면 뇌는 낮으로 착각하여 수면 유도 호르몬인 멜라토닌 분비를 억제합니다. 불면증을 예방하기 위해 취침 1시간 전 스마트폰 사용을 멈춰야 하는 과학적 이유를 설명합니다.', '/images/hs/hs_42.jpg', now() - INTERVAL '34 days', 7890),
-- 43. 만성 피로 증후군
(1, '자도 자도 피곤하다면? 단순 피로가 아닌 ''만성 피로 증후군'' 의심', '충분히 휴식을 취해도 6개월 이상 극심한 피로가 지속되고, 기억력 감퇴, 근육통, 수면 장애가 동반된다면 만성 피로 증후군을 의심해봐야 합니다. 전문가의 진단을 통해 기저 질환을 감별하고 생활 습관을 교정하는 방법을 알아봅니다.', '/images/hs/hs_43.jpg', now() - INTERVAL '29 days', 5420),
-- 44. 유방암 자가검진
(2, '여성암 발생 1위 유방암, 매월 생리 끝난 후 3일째 자가검진 필수', '유방암은 조기 발견 시 생존율이 95% 이상으로 매우 높습니다. 매월 정기적인 자가검진을 통해 가슴에 만져지는 멍울이나 피부의 변화가 없는지 확인하는 올바른 검진 방법과, 40세 이상 정기 유방촬영술의 중요성을 강조합니다.', '/images/hs/hs_44.jpg', now() - INTERVAL '24 days', 4150),
-- 45. 폐렴 예방
(3, '감기로 오해하기 쉬운 노년층의 불청객, 치명적인 ''폐렴'' 예방 접종', '폐렴은 국내 사망 원인 3위에 오를 만큼 무서운 질환이지만, 초기 증상이 감기와 비슷해 발견이 늦어지기 쉽습니다. 65세 이상 어르신들에게 강력히 권장되는 폐렴구균 예방접종과 면역력 관리 수칙을 상세히 안내해 드립니다.', '/images/hs/hs_45.jpg', now() - INTERVAL '15 days', 2310),
-- 46. 독감과 감기의 차이
(1, '기침 나고 열이 펄펄, 이건 독감일까 감기일까? (증상 비교 및 대처법)', '감기는 다양한 바이러스에 의해 서서히 나타나지만, 독감은 인플루엔자 바이러스에 의해 고열과 전신 근육통을 동반하며 갑작스럽게 발병합니다. 두 질환의 명확한 차이점과 항바이러스제 처방 시기를 놓치지 않는 방법을 설명합니다.', '/images/hs/hs_46.jpg', now() - INTERVAL '2 days', 8920);

-- ==================================================

-- 고객의소리 미답변 10개
INSERT INTO voc (mem_id, title, content, upload_img, write_date, admin_staff_id, answer_content, answer_write_date, answer_status) VALUES
(1, '소아과 간호사님 칭찬합니다', '아이가 병원을 무서워해서 많이 울었는데, 소아과 간호사님께서 장난감으로 달래주시고 주사도 안 아프게 놔주셔서 정말 감사했습니다.', '/images/voc/voc_01.jpg', now() - INTERVAL '30 days', NULL, NULL, NULL, FALSE),
(1, '주차장 공간이 너무 부족합니다', '아침 진료 예약이라 일찍 왔는데도 만차라서 주차하는 데만 30분이 걸렸습니다. 주차 공간 개선이 시급해 보입니다.', '/images/voc/voc_02.jpg', now() - INTERVAL '28 days', NULL, NULL, NULL, FALSE),
(1, '대기실 의자가 부족해요', '어르신들이 많이 오시는데 정형외과 앞 대기실 의자가 부족해서 서 계시는 분들이 많았습니다. 대기 공간에 의자를 더 늘려주세요.', '/images/voc/voc_03.jpg', now() - INTERVAL '25 days', NULL, NULL, NULL, FALSE),
(1, '2층 수납 키오스크 오류', '어제 오후에 2층에서 키오스크로 카드 결제를 하려는데 계속 오류가 나서 결국 창구에서 한참 기다렸습니다. 기기 점검 부탁드립니다.', '/images/voc/voc_04.jpg', now() - INTERVAL '20 days', NULL, NULL, NULL, FALSE),
(1, '내과 원장님 상세한 설명 감사합니다', '검사 결과에 대해 걱정이 많았는데, 내과 원장님께서 제가 이해하기 쉽게 사진을 보여주시며 꼼꼼히 설명해주셔서 너무 안심이 되었습니다.', '/images/voc/voc_05.jpg', now() - INTERVAL '15 days', NULL, NULL, NULL, FALSE),
(1, '환자식 온도가 너무 차갑습니다', '어머니께서 입원 중이신데, 저녁 배식 때 국이랑 반찬이 다 식어서 나왔다고 하시네요. 식사 보온에 조금 더 신경 써주시면 좋겠습니다.', '/images/voc/voc_06.jpg', now() - INTERVAL '10 days', NULL, NULL, NULL, FALSE),
(2, '응급실 의료진분들 정말 고생 많으십니다', '새벽에 급성 장염으로 응급실에 갔는데, 정신없이 바쁘신 와중에도 간호사님들과 당직 의사 선생님께서 너무 친절하게 처치해 주셨습니다. 진심으로 감사합니다.', '/images/voc/voc_07.jpg', now() - INTERVAL '5 days', NULL, NULL, NULL, FALSE),
(2, '모바일 앱 로그인 오류가 자주 발생합니다', '어제부터 앱으로 진료 예약을 하려고 하는데 지문 인증 로그인 단계에서 앱이 자꾸 종료됩니다. 빠른 확인 및 수정 부탁드립니다.', '/images/voc/voc_08.jpg', now() - INTERVAL '2 days', NULL, NULL, NULL, FALSE),
(3, '본관 1층 화장실 청결 상태 개선 요청', '본관 1층 여자 화장실 세면대 쪽에 휴지가 널브러져 있고 바닥이 물기 때문에 미끄러워 위험해 보였습니다. 미화원분들의 청소 주기를 늘려주세요.', '/images/voc/voc_09.jpg', now() - INTERVAL '1 days', NULL, NULL, NULL, FALSE),
(3, '셔틀버스 기사님 서비스 교육이 필요합니다', '오늘 아침 9시 타임 지하철역에서 출발하는 셔틀버스를 탔는데, 거동이 불편하신 어르신이 늦게 타셨다고 기사님이 짜증을 내시더라고요. 친절 교육이 필요해 보입니다.', '/images/voc/voc_10.jpg', now(), NULL, NULL, NULL, FALSE);
