import json
import pymysql
import pandas as pd
from datetime import datetime

import boto3
from botocore.exceptions import ClientError

# ✅ AWS Secrets Manager에서 시크릿 가져오기
def get_db_secret(secret_name, region_name="ap-northeast-2"):
    session = boto3.session.Session()
    client = session.client(
        service_name="secretsmanager",
        region_name=region_name
    )

    try:
        get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    except ClientError as e:
        raise Exception(f"Secrets Manager 에러: {e}")

    if "SecretString" in get_secret_value_response:
        secret = get_secret_value_response["SecretString"]
        return json.loads(secret)
    else:
        raise Exception("시크릿 문자열이 비어 있음")

# ✅ 티켓 정보 DB 저장
def insert_to_mysql(detail_data):
    db_secret = get_db_secret("my_db_secret")  # 🔁 시크릿 이름은 실제 값으로 바꾸기

    conn = pymysql.connect(
        host=db_secret['host'],
        user=db_secret['username'],
        password=db_secret['password'],
        db=db_secret['dbname'],
        port=int(db_secret.get('port', 3306)),
        charset='utf8mb4'
    ) 
    cursor = conn.cursor()

    insert_sql = """
        INSERT INTO ticket (
            ticket_title,
            ticket_price,
            ticket_venue,
            ticket_age_limit,
            ticket_booking_link,
            ticket_booking_provider,
            ticket_booking_datetime,
            ticket_created_at,
            ticket_image_url,
            category_id,
            ticket_event_start_datetime,
            ticket_event_end_datetime,
            ticket_event_time,
            ticket_description_url
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE
            ticket_title = VALUES(ticket_title),
            ticket_price = VALUES(ticket_price),
            ticket_venue = VALUES(ticket_venue),
            ticket_age_limit = VALUES(ticket_age_limit),
            ticket_booking_provider = VALUES(ticket_booking_provider),
            ticket_booking_datetime = VALUES(ticket_booking_datetime),
            ticket_created_at = VALUES(ticket_created_at),
            ticket_image_url = VALUES(ticket_image_url),
            category_id = VALUES(category_id),
            ticket_event_start_datetime = VALUES(ticket_event_start_datetime),
            ticket_event_end_datetime = VALUES(ticket_event_end_datetime),
            ticket_event_time = VALUES(ticket_event_time),
            ticket_description_url = VALUES(ticket_description_url)
    """

    try:
        def safe(val):
            return None if pd.isnull(val) or str(val).strip().lower() in ["없음", "nan"] else str(val)

        def safe_datetime(val):
            if pd.isnull(val):
                return None
            s = str(val).strip()
            if s.lower() in ["없음", "nan", "예매링크 참조"]:
                return None
            try:
                return pd.to_datetime(s)
            except:
                return None

        desc = detail_data.get("ticket_description_url", "없음")
        if isinstance(desc, list):
            desc = json.dumps(desc)

        cursor.execute(insert_sql, (
            safe(detail_data.get("ticket_title")),
            safe(detail_data.get("ticket_price")),
            safe(detail_data.get("ticket_venue")),
            safe(detail_data.get("ticket_age_limit", "없음")),
            safe(detail_data.get("ticket_booking_link")),
            safe(detail_data.get("ticket_booking_provider")),
            safe_datetime(detail_data.get("ticket_booking_datetime")),
            safe_datetime(detail_data.get("ticket_created_at")),
            safe(detail_data.get("ticket_image_url")),
            int(detail_data["category_id"]) if detail_data.get("category_id") is not None else None,
            safe_datetime(detail_data.get("ticket_event_start_datetime")),
            safe_datetime(detail_data.get("ticket_event_end_datetime")),
            safe(detail_data.get("ticket_event_time", "없음")),
            safe(desc)
        ))

    except Exception as e:
        print(f"❌ UPSERT 실패 - {detail_data.get('ticket_title', '제목없음')}: {e}")

    conn.commit()
    cursor.close()
    conn.close()
    print(f"✅ DB 저장 완료: {detail_data.get('ticket_title', '제목없음')}")
