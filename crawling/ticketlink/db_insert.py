import pymysql
import pandas as pd
from datetime import datetime

def insert_to_mysql(detail_data):
    # ✅ MySQL 연결
    conn = pymysql.connect(
        host="43.201.218.86",
        port=3306,
        user="jojinse",
        password="1234",
        database="ticketplanet",
        charset="utf8mb4"
    )
    cursor = conn.cursor()

    # ✅ insert 쿼리 (UPsert 포함)
    insert_sql = """
        INSERT INTO ticket (
            ticket_title,
            ticket_price,
            ticket_description,
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
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE
            ticket_title = VALUES(ticket_title),
            ticket_price = VALUES(ticket_price),
            ticket_description = VALUES(ticket_description),
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

        cursor.execute(insert_sql, (
            safe(detail_data.get("ticket_title")),
            safe(detail_data.get("ticket_price")),
            safe(detail_data.get("ticket_description", "없음")),
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
            safe(detail_data.get("ticket_description_url", "없음"))
        ))

    except Exception as e:
        print(f"❌ UPSERT 실패 - {detail_data.get('ticket_title', '제목없음')}: {e}")

    conn.commit()
    cursor.close()
    conn.close()
    print(f"✅ DB 저장 완료: {detail_data.get('ticket_title', '제목없음')}")
