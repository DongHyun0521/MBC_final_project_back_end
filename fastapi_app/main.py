from fastapi import FastAPI, HTTPException
import os
import json
import joblib
import numpy as np
import pandas as pd
import psycopg2
from psycopg2.extras import RealDictCursor

from EV_data import add_time_series_features
from LightGBM_train_ttr import InputFeatures, diagnose

app = FastAPI(title="EV Charger Predictive Maintenance API")


# =========================================================
# 1. 경로 설정
# =========================================================
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

STATUS_MODEL_PATH = os.path.join(BASE_DIR, "models", "best_lightgbm_model.pkl")
STATUS_FEATURE_PATH = os.path.join(BASE_DIR, "models", "lightgbm_feature_columns.json")
REFERENCE_PATH = os.path.join(BASE_DIR, "model_data", "train.csv")


# =========================================================
# 2. DB 설정
# =========================================================
DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "dbname": "myDB",
    "user": "postgres",
    "password": "1234",
}


# =========================================================
# 3. 모델 / 설정 로드
# =========================================================
for path, name in [
    (STATUS_MODEL_PATH, "상태 모델"),
    (STATUS_FEATURE_PATH, "상태 feature"),
]:
    if not os.path.exists(path):
        raise FileNotFoundError(f"{name} 파일이 없습니다: {path}")

status_model = joblib.load(STATUS_MODEL_PATH)

with open(STATUS_FEATURE_PATH, "r", encoding="utf-8") as f:
    status_feature_cols = json.load(f)

reference_df = None
reference_stats = {}

if os.path.exists(REFERENCE_PATH):
    reference_df = pd.read_csv(REFERENCE_PATH)
    reference_df.columns = reference_df.columns.str.strip()

    candidate_cols = [
        "Peak_T", "Peak_T_ma7", "Peak_T_ma14",
        "Health", "Health_ma14",
        "Current", "Current_std14",
        "Voltage_std14",
        "Temp_Change", "Health_Change",
    ]

    for col in candidate_cols:
        if col in reference_df.columns:
            ref_series = pd.to_numeric(reference_df[col], errors="coerce").dropna()
            if len(ref_series) > 0:
                mean = float(ref_series.mean())
                std = float(ref_series.std())
                if std == 0 or np.isnan(std):
                    std = 1.0
                reference_stats[col] = {"mean": mean, "std": std}

print("✅ 상태 모델 로드 완료")
print(f"✅ 상태 feature 개수: {len(status_feature_cols)}")
print("✅ ttr.py 연동 완료")


# =========================================================
# 4. 공통 유틸
# =========================================================
def get_connection():
    return psycopg2.connect(
        host=DB_CONFIG["host"],
        port=DB_CONFIG["port"],
        dbname=DB_CONFIG["dbname"],
        user=DB_CONFIG["user"],
        password=DB_CONFIG["password"],
    )


def add_static_columns(df: pd.DataFrame) -> pd.DataFrame:
    out = df.copy()

    if "ID" not in out.columns:
        out["ID"] = "TEMP_DEVICE"

    if "Spec_Val" not in out.columns:
        spec_map = {
            "AC_7kW": 7,
            "DC_50kW": 50,
            "DC_100kW": 100,
        }
        if "Spec" not in out.columns:
            raise HTTPException(status_code=400, detail="Spec 컬럼이 필요합니다.")
        out["Spec_Val"] = out["Spec"].map(spec_map).fillna(7)

    if "Loc_Val" not in out.columns:
        if "Loc" not in out.columns:
            raise HTTPException(status_code=400, detail="Loc 컬럼이 필요합니다.")
        out["Loc_Val"] = out["Loc"].apply(lambda x: 1 if x == "Outdoor" else 0)

    return out


def align_features(df: pd.DataFrame, feature_cols: list[str]) -> pd.DataFrame:
    aligned = df.copy()

    for col in feature_cols:
        if col not in aligned.columns:
            aligned[col] = 0.0

    aligned = aligned[feature_cols].copy()
    aligned = aligned.replace([np.inf, -np.inf], np.nan).fillna(0)

    return aligned


def class_to_status(pred_class: int) -> str:
    mapping = {
        0: "정상",
        1: "점검",
        2: "위험",
    }
    return mapping.get(pred_class, "알 수 없음")


def class_to_action(pred_class: int) -> str:
    mapping = {
        0: "모니터링",
        1: "점검 필요",
        2: "현장 출동 및 작동 중지",
    }
    return mapping.get(pred_class, "상태 확인 필요")


def class_to_message(pred_class: int) -> str:
    mapping = {
        0: "현재 정상",
        1: "이상 징후 감지, 점검 필요",
        2: "위험 상태 진입, 즉시 조치 필요",
    }
    return mapping.get(pred_class, "상태 확인 필요")


def feature_to_reason(feature: str, pred_class: int) -> str:
    warning_mapping = {
        "Peak_T": "내부 온도 상승",
        "Peak_T_ma7": "내부 온도 상승 감지",
        "Peak_T_ma14": "내부 온도 상승 감지",
        "Health": "기기 수명 저하",
        "Health_ma14": "기기 수명 저하",
        "Current": "전류 상승",
        "Current_std14": "전류 변동 감지",
        "Voltage_std14": "전압 변동 감지",
        "Temp_Change": "온도 변화 감지",
        "Health_Change": "기기 수명 감소",
    }

    risk_mapping = {
        "Peak_T": "과열 상태",
        "Peak_T_ma7": "과열 상태",
        "Peak_T_ma14": "과열 상태",
        "Health": "배터리 성능 저하",
        "Health_ma14": "기기 수명 저하",
        "Current": "과전류 발생",
        "Current_std14": "과전류 발생",
        "Voltage_std14": "전압 이상",
        "Temp_Change": "급격한 온도 상승",
        "Health_Change": "기기 수명 저하",
    }

    if pred_class == 1:
        return warning_mapping.get(feature, "이상 징후 감지")
    if pred_class == 2:
        return risk_mapping.get(feature, "이상 상태 발생")
    return ""


def extract_top_reason(
    latest_row: pd.Series,
    feature_cols: list[str],
    model,
    pred_class: int,
) -> str:
    if pred_class == 0:
        return ""

    candidates = [
        "Peak_T", "Peak_T_ma7", "Peak_T_ma14",
        "Health", "Health_ma14",
        "Current", "Current_std14",
        "Voltage_std14",
        "Temp_Change", "Health_Change",
    ]

    importances = dict(zip(feature_cols, model.feature_importances_))
    scored: list[tuple[str, float]] = []

    for f in candidates:
        if f not in latest_row.index or f not in importances:
            continue
        if f not in reference_stats:
            continue

        try:
            val = float(latest_row[f])
        except (TypeError, ValueError):
            continue

        mean = reference_stats[f]["mean"]
        std = reference_stats[f]["std"]
        z = abs((val - mean) / std)
        score = z * float(importances[f])
        scored.append((f, score))

    if not scored:
        for f in candidates:
            if f in latest_row.index:
                return feature_to_reason(f, pred_class)
        return "이상 징후 감지" if pred_class == 1 else "이상 상태 발생"

    scored.sort(key=lambda x: x[1], reverse=True)
    top_feature = scored[0][0]
    return feature_to_reason(top_feature, pred_class).strip()


def build_response(
    pred_class: int,
    status: str,
    action: str,
    alarm: bool,
    message: str,
    main_reason: str,
    status_probs: np.ndarray,
    ttr_result: dict,
    is_operating: bool,
    inspection_requested: bool,
) -> dict:
    fault_prob_7d = ttr_result.get("fault_prob_7d")

    if status == "정상":
        return {
            "pred_class": pred_class,
            "status": status,
            "message": "현재 이상 없음",
            "prob_normal": float(status_probs[0]),
            "prob_warning": float(status_probs[1]),
            "prob_risk": float(status_probs[2]),
        }

    if status == "점검":
        return {
            "pred_class": pred_class,
            "status": status,
            "action": action,
            "alarm": alarm,
            "message": message,
            "main_reason": main_reason,
            "future_risk": f"7일 내 고장확률: {fault_prob_7d * 100:.1f}%" if fault_prob_7d is not None else "",
            "fault_prob_7d": fault_prob_7d,
            "inspection_requested": inspection_requested,
            "prob_normal": float(status_probs[0]),
            "prob_warning": float(status_probs[1]),
            "prob_risk": float(status_probs[2]),
        }

    return {
        "pred_class": pred_class,
        "status": status,
        "action": action,
        "alarm": alarm,
        "message": message,
        "main_reason": main_reason,
        "device_status": "작동 중" if is_operating else "위험 감지로 인한 강제 중지",
        "inspection_requested": inspection_requested,
        "prob_normal": float(status_probs[0]),
        "prob_warning": float(status_probs[1]),
        "prob_risk": float(status_probs[2]),
    }


def validate_history_df(df: pd.DataFrame):
    required_cols = [
        "Day", "Usage_Hrs", "Daily_KWh", "Total_KWh",
        "Voltage", "Current", "Peak_T", "Health",
        "Temp_Change", "Health_Change", "Spec", "Loc"
    ]
    missing = [c for c in required_cols if c not in df.columns]
    if missing:
        raise HTTPException(
            status_code=400,
            detail=f"입력 데이터에 필요한 컬럼이 없습니다: {missing}"
        )


def run_prediction_from_history(
    history: list,
    is_operating: bool,
    inspection_requested: bool,
) -> dict:
    if not isinstance(history, list) or len(history) == 0:
        raise HTTPException(status_code=400, detail="history는 비어 있지 않은 리스트여야 합니다.")

    df = pd.DataFrame(history)
    df.columns = df.columns.str.strip()

    validate_history_df(df)

    df = add_static_columns(df)
    df = add_time_series_features(df)

    latest = df.iloc[-1].copy()

    X_status = pd.DataFrame([latest])
    X_status = align_features(X_status, status_feature_cols)

    status_probs = status_model.predict_proba(X_status)[0]
    pred_class = int(np.argmax(status_probs))

    status = class_to_status(pred_class)
    action = class_to_action(pred_class)
    message = class_to_message(pred_class)
    alarm = pred_class in [1, 2]

    main_reason = extract_top_reason(
        latest_row=latest,
        feature_cols=status_feature_cols,
        model=status_model,
        pred_class=pred_class,
    )

    ttr_features = InputFeatures(
        health=float(latest["Health"]),
        peak_temp=float(latest["Peak_T"]),
        usage_hours=float(latest["Usage_Hrs"]),
        is_already_fault=(pred_class == 2),
    )

    ttr_result = diagnose(ttr_features).to_dict()

    return build_response(
        pred_class=pred_class,
        status=status,
        action=action,
        alarm=alarm,
        message=message,
        main_reason=main_reason,
        status_probs=status_probs,
        ttr_result=ttr_result,
        is_operating=is_operating,
        inspection_requested=inspection_requested,
    )


# =========================================================
# 5. DB 조회 유틸
# =========================================================
def fetch_prediction_input_from_db(charger_id: int, limit: int = 14) -> tuple[list[dict], bool, bool]:
    conn = None
    cur = None

    try:
        if limit <= 0:
            raise HTTPException(status_code=400, detail="limit는 1 이상이어야 합니다.")

        conn = get_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)

        state_query = """
            SELECT
                CASE
                    WHEN c.ev_charger_power = TRUE THEN TRUE
                    ELSE FALSE
                END AS is_operating,
                CASE
                    WHEN EXISTS (
                        SELECT 1
                        FROM ev_inspection_request r
                        WHERE r.ev_charger_id = c.ev_charger_id
                          AND r.request_status IN ('REQUESTED', 'IN_PROGRESS')
                    )
                    THEN TRUE
                    ELSE FALSE
                END AS inspection_requested,
                c.ev_charger_type,
                ps.parking_floor
            FROM ev_charger c
            JOIN parking_spot ps
              ON ps.parking_spot_id = c.parking_spot_id
            WHERE c.ev_charger_id = %s
        """
        cur.execute(state_query, (charger_id,))
        state_row = cur.fetchone()

        if not state_row:
            raise HTTPException(status_code=404, detail=f"charger_id={charger_id} 충전기 정보가 없습니다.")

        history_query = """
            SELECT *
            FROM (
                SELECT
                    ROW_NUMBER() OVER (ORDER BY s.measured_time ASC) AS "Day",
                    COALESCE(cs.charging_duration_minutes / 60.0, 0) AS "Usage_Hrs",
                    COALESCE(cs.charged_kwh, 0) AS "Daily_KWh",
                    COALESCE(
                        SUM(COALESCE(cs.charged_kwh, 0)) OVER (
                            ORDER BY s.measured_time ASC
                            ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
                        ), 0
                    ) AS "Total_KWh",
                    COALESCE(s.voltage, 0) AS "Voltage",
                    COALESCE(s.current, 0) AS "Current",
                    COALESCE(s.internal_temperature, 0) AS "Peak_T",
                    COALESCE(s.health_score, 0) AS "Health",
                    COALESCE(s.temperature_change, 0) AS "Temp_Change",
                    COALESCE(s.health_change, 0) AS "Health_Change",
                    CASE
                        WHEN c.ev_charger_type = 'FAST' THEN 'DC_100kW'
                        ELSE 'AC_7kW'
                    END AS "Spec",
                    CASE
                        WHEN ps.parking_floor >= 1 THEN 'Indoor'
                        ELSE 'Outdoor'
                    END AS "Loc",
                    CONCAT('EV_', c.ev_charger_id) AS "ID",
                    s.measured_time
                FROM ev_sensor_log s
                JOIN ev_charger c
                  ON c.ev_charger_id = s.ev_charger_id
                JOIN parking_spot ps
                  ON ps.parking_spot_id = c.parking_spot_id
                LEFT JOIN LATERAL (
                    SELECT cs.*
                    FROM ev_charging_session cs
                    WHERE cs.ev_charger_id = s.ev_charger_id
                      AND cs.session_start_time <= s.measured_time
                    ORDER BY cs.session_start_time DESC
                    LIMIT 1
                ) cs ON TRUE
                WHERE s.ev_charger_id = %s
                ORDER BY s.measured_time DESC
                LIMIT %s
            ) t
            ORDER BY t.measured_time ASC
        """
        cur.execute(history_query, (charger_id, limit))
        rows = cur.fetchall()

        if not rows:
            raise HTTPException(status_code=404, detail=f"charger_id={charger_id} 이력 데이터가 없습니다.")

        history = []
        for row in rows:
            row_dict = dict(row)
            row_dict.pop("measured_time", None)
            history.append(row_dict)

        is_operating = bool(state_row["is_operating"])
        inspection_requested = bool(state_row["inspection_requested"])

        return history, is_operating, inspection_requested

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"DB 예측 입력 조회 실패: {str(e)}")
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()

def insert_prediction_result(conn, charger_id, result, latest_row):
    with conn.cursor() as cur:
        cur.execute("""
            INSERT INTO ev_prediction_result (
                ev_charger_id,
                predicted_time,
                ai_status,
                risk_score,
                fault_prob_7d,
                prob_normal,
                prob_warning,
                prob_risk,
                main_reason,
                action_message,
                internal_temperature,
                voltage,
                current,
                health_score,
                communication_status,
                created_time
            )
            VALUES (
                %s, now(), %s, %s, %s, %s, %s, %s,
                %s, %s,
                %s, %s, %s, %s, %s,
                now()
            )
        """, (
            charger_id,
            result.get("ai_status"),
            result.get("risk_score"),
            result.get("fault_prob_7d"),
            result.get("prob_normal"),
            result.get("prob_warning"),
            result.get("prob_risk"),
            result.get("main_reason"),
            result.get("action"),
            latest_row.get("Peak_T"),
            latest_row.get("Voltage"),
            latest_row.get("Current"),
            latest_row.get("Health"),
            "ONLINE"
        ))
# 상태 변환함수
def map_status_to_db(status):
    if status == "정상":
        return "NORMAL"
    elif status == "점검":
        return "CHECK"
    elif status == "위험":
        return "RISK"
    return "NORMAL"
# 상태 변환 함수
def map_status_to_history(ai_status):
    if ai_status == "NORMAL":
        return "STANDBY"
    elif ai_status == "CHECK":
        return "CHECK"
    elif ai_status == "RISK":
        return "RISK"
    return "STANDBY"

# 결과 자동 저장 INSERT
def insert_status_history(conn, charger_id, status_code, status_reason):
    with conn.cursor() as cur:
        cur.execute("""
            INSERT INTO ev_charger_status_history (
                ev_charger_id,
                status_code,
                status_reason,
                changed_time
            )
            VALUES (%s, %s, %s, now())
        """, (
            charger_id,
            status_code,
            status_reason
        ))
# 같은 상태 계속 저장안되게 설정
def is_same_status(conn, charger_id, new_status):
    with conn.cursor() as cur:
        cur.execute("""
            SELECT status_code
            FROM ev_charger_status_history
            WHERE ev_charger_id = %s
            ORDER BY changed_time DESC
            LIMIT 1
        """, (charger_id,))
        row = cur.fetchone()

        if not row:
            return False

        return row[0] == new_status
# =========================================================
# 6. 기본 엔드포인트
# =========================================================
@app.get("/")
def home():
    return {"message": "OK"}


@app.get("/health")
def health_check():
    return {
        "status": "ok",
        "status_model_loaded": True,
        "status_num_features": len(status_feature_cols),
        "ttr_linked": True,
    }


# =========================================================
# 7. 기존 테스트용 예측 엔드포인트 유지
# =========================================================
@app.post("/predict")
def predict(data: dict):
    if "history" not in data:
        raise HTTPException(status_code=400, detail="history 필드가 필요합니다.")

    history = data["history"]
    is_operating = data.get("is_operating", True)
    inspection_requested = data.get("inspection_requested", False)

    return run_prediction_from_history(
        history=history,
        is_operating=is_operating,
        inspection_requested=inspection_requested,
    )


# =========================================================
# 8. DB 연동용 예측 엔드포인트
# =========================================================
@app.get("/predict/db/{charger_id}")
def predict_from_db(charger_id: int, limit: int = 14):
    history, is_operating, inspection_requested = fetch_prediction_input_from_db(
        charger_id=charger_id,
        limit=limit,
    )

    # 예측 수행
    result = run_prediction_from_history(
        history=history,
        is_operating=is_operating,
        inspection_requested=inspection_requested,
    )

    # DB 저장용 값 가공
    ai_status = map_status_to_db(result["status"])
    history_status = map_status_to_history(ai_status)

    latest = history[-1]  # 마지막 시점 데이터

    db_result = {
        "ai_status": ai_status,
        "risk_score": result.get("prob_risk"),
        "fault_prob_7d": result.get("fault_prob_7d"),
        "prob_normal": result.get("prob_normal"),
        "prob_warning": result.get("prob_warning"),
        "prob_risk": result.get("prob_risk"),
        "main_reason": None if ai_status == "NORMAL" else result.get("main_reason"),
        "action": None if ai_status == "NORMAL" else result.get("action"),
    }

    conn = None
    try:
        conn = get_connection()

        # 1) 예측 결과 저장
        insert_prediction_result(conn, charger_id, db_result, latest)

        # 2) 상태 이력 저장 (직전 상태와 다를 때만)
        status_reason = None
        if history_status in ["CHECK", "RISK"]:
            status_reason = db_result.get("main_reason")

        if not is_same_status(conn, charger_id, history_status):
            insert_status_history(conn, charger_id, history_status, status_reason)

        conn.commit()

    except Exception as e:
        if conn:
            conn.rollback()
        raise HTTPException(status_code=500, detail=f"예측 결과 저장 실패: {str(e)}")

    finally:
        if conn:
            conn.close()

    return result

# 최신 예측 결과 조회
@app.get("/prediction/latest/{charger_id}")
def get_latest_prediction(charger_id: int):
    conn = None
    cur = None

    try:
        conn = get_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)

        cur.execute("""
            SELECT
                prediction_id,
                ev_charger_id,
                predicted_time,
                ai_status,
                risk_score,
                fault_prob_7d,
                prob_normal,
                prob_warning,
                prob_risk,
                main_reason,
                action_message,
                internal_temperature,
                voltage,
                current,
                health_score,
                communication_status,
                created_time
            FROM ev_prediction_result
            WHERE ev_charger_id = %s
            ORDER BY predicted_time DESC
            LIMIT 1
        """, (charger_id,))

        row = cur.fetchone()

        if not row:
            raise HTTPException(status_code=404, detail=f"charger_id={charger_id} 예측 결과가 없습니다.")

        return dict(row)

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"최신 예측 결과 조회 실패: {str(e)}")
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()
# 상태 이력 조회 
@app.get("/status-history/{charger_id}")
def get_status_history(charger_id: int, limit: int = 10):
    conn = None
    cur = None

    try:
        conn = get_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)

        cur.execute("""
            SELECT
                status_history_id,
                ev_charger_id,
                status_code,
                status_reason,
                changed_time
            FROM ev_charger_status_history
            WHERE ev_charger_id = %s
            ORDER BY changed_time DESC
            LIMIT %s
        """, (charger_id, limit))

        rows = cur.fetchall()
        return [dict(row) for row in rows]

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"상태 이력 조회 실패: {str(e)}")
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()

#센서 추이 그래프 조회 
@app.get("/sensor-history/{charger_id}")
def get_sensor_history(charger_id: int, limit: int = 30):
    conn = None
    cur = None

    try:
        conn = get_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)

        cur.execute("""
            SELECT
                sensor_log_id,
                ev_charger_id,
                measured_time,
                internal_temperature,
                temperature_change,
                voltage,
                voltage_change,
                current,
                current_change,
                health_score,
                health_change,
                communication_status
            FROM ev_sensor_log
            WHERE ev_charger_id = %s
            ORDER BY measured_time DESC
            LIMIT %s
        """, (charger_id, limit))

        rows = cur.fetchall()
        return [dict(row) for row in rows]

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"센서 이력 조회 실패: {str(e)}")
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()

# 충전기 목록 + 최신 상태 요약
@app.get("/chargers/summary")
def get_chargers_summary():
    conn = None
    cur = None

    try:
        conn = get_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)

        cur.execute("""
            SELECT
                c.ev_charger_id,
                c.parking_spot_id,
                c.ev_charger_type,
                c.ev_charger_state,
                c.ev_charger_power,
                ps.parking_floor,

                h.status_code,
                h.status_reason,
                h.changed_time,

                p.ai_status,
                p.risk_score,
                p.fault_prob_7d,
                p.main_reason,
                p.action_message,
                p.predicted_time

            FROM ev_charger c
            JOIN parking_spot ps
              ON ps.parking_spot_id = c.parking_spot_id

            LEFT JOIN LATERAL (
                SELECT
                    status_code,
                    status_reason,
                    changed_time
                FROM ev_charger_status_history h
                WHERE h.ev_charger_id = c.ev_charger_id
                ORDER BY h.changed_time DESC
                LIMIT 1
            ) h ON TRUE

            LEFT JOIN LATERAL (
                SELECT
                    ai_status,
                    risk_score,
                    fault_prob_7d,
                    main_reason,
                    action_message,
                    predicted_time
                FROM ev_prediction_result p
                WHERE p.ev_charger_id = c.ev_charger_id
                ORDER BY p.predicted_time DESC
                LIMIT 1
            ) p ON TRUE

            ORDER BY ps.parking_floor, c.ev_charger_id
        """)

        rows = cur.fetchall()
        return [dict(row) for row in rows]

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"충전기 요약 조회 실패: {str(e)}")
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()

