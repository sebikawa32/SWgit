import pymysql
import os
import json
from datetime import datetime
from pytz import timezone
import boto3

KST = timezone("Asia/Seoul")

def get_db_secret(secret_name):
    """AWS SecretsManager에서 DB 비밀정보 가져오기"""
    region_name = "ap-northeast-2"  # 원하는 리전으로 변경
    session = boto3.session.Session()
    client = session.client(service_name='secretsmanager', region_name=region_name)
    get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    secret = get_secret_value_response['SecretString']
    return json.loads(secret)

def get_db_conn(secret):
    """MySQL DB 커넥션 생성"""
    return pymysql.connect(
        host=secret["host"],
        user=secret["username"],
        password=secret["password"],
        db=secret["dbname"],
        port=int(secret.get("port", 3306)),
        charset="utf8mb4",
        autocommit=True,
        cursorclass=pymysql.cursors.DictCursor
    )

def lambda_handler(event=None, context=None):
    now = datetime.now(KST)
    print(f"🚀 Lambda 시작: {now}")

    # 환경변수에 저장된 시크릿 이름 사용 (람다에 세팅!)
    secret_name = os.environ.get("DB_SECRET_NAME")
    if not secret_name:
        raise Exception("DB_SECRET_NAME 환경변수가 설정되지 않았습니다.")

    # 시크릿에서 DB정보 불러오기
    db_secret = get_db_secret(secret_name)
    conn = get_db_conn(db_secret)

    try:
        with conn.cursor() as cur:
            # 1. 알림 대상 조회
            cur.execute("""
                SELECT
                    uas.user_id,
                    uas.ticket_id,
                    t.ticket_title,
                    t.ticket_booking_datetime,
                    uas.alert_minutes_before
                FROM
                    user_alert_setting uas
                JOIN
                    ticket t ON uas.ticket_id = t.ticket_id
                WHERE
                    TIMESTAMPDIFF(MINUTE, %s, t.ticket_booking_datetime) = uas.alert_minutes_before
            """, (now.strftime("%Y-%m-%d %H:%M:%S"),))
            targets = cur.fetchall()
            print(f"🔍 알림 후보: {len(targets)}건")

            for row in targets:
                user_id = row["user_id"]
                ticket_id = row["ticket_id"]
                ticket_title = row["ticket_title"]
                alert_type = "DDAY"
                alert_minutes = row["alert_minutes_before"]

                # 알림 메시지
                if alert_minutes % 1440 == 0:
                    before_str = f"D-{alert_minutes // 1440}"
                elif alert_minutes % 60 == 0:
                    before_str = f"{alert_minutes // 60}시간 전"
                else:
                    before_str = f"{alert_minutes}분 전"

                content = f"[{before_str}] {ticket_title} 예매일이 곧 시작됩니다!"
                url = f"/ticket/{ticket_id}"  # 실제 프론트 경로에 맞게

                # 알림 중복 체크 (오늘 같은 알림 있으면 스킵)
                cur.execute("""
                    SELECT 1 FROM notification
                    WHERE user_id=%s AND type=%s AND content=%s
                        AND target_id=%s AND DATE(created_at) = CURDATE()
                    LIMIT 1
                """, (user_id, alert_type, content, ticket_id))
                already = cur.fetchone()
                if already:
                    print(f"⚠️ 이미 보낸 알림: user={user_id}, ticket={ticket_id}, content={content}")
                    continue

                # 알림 저장
                cur.execute("""
                    INSERT INTO notification (user_id, type, content, url, target_type, target_id, is_read, created_at)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                """, (
                    user_id,
                    alert_type,
                    content,
                    url,
                    "TICKET",
                    ticket_id,
                    False,
                    now.strftime("%Y-%m-%d %H:%M:%S")
                ))
                print(f"✅ 알림 생성: user={user_id}, ticket={ticket_id}, content={content}")

        print("🎉 Lambda 정상 종료")
    finally:
        conn.close()
