import pandas as pd
import pymysql
from datetime import datetime

def insert_to_mysql():
    # ✅ CSV 파일 불러오기
    df = pd.read_csv("data/merged_ticket_data.csv")

    # ✅ MySQL 연결 설정
    conn = pymysql.connect(
        host="43.201.218.86",     # 너의 서버 주소
        port=3306,
        user="jojinse",
        password="1234",       # 여기에 실제 비밀번호 넣기
        database="ticketplanet",
        charset="utf8mb4"
    )
    cursor = conn.cursor()

    insert_sql = """
        INSERT INTO ticket (
            ticket_title,
            ticket_event_datetime,
            ticket_price,
            ticket_description,
            ticket_venue,
            ticket_booking_link,
            ticket_booking_provider,
            ticket_booking_datetime,
            ticket_created_at,
            ticket_image_url,
            category_id
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """

    for idx, row in df.iterrows():
        try:
            # ✅ 중복 여부 확인 (ticket_booking_link 기준)
            check_sql = "SELECT COUNT(*) FROM ticket WHERE ticket_booking_link = %s"
            cursor.execute(check_sql, (row["ticket_booking_link"],))
            exists = cursor.fetchone()[0]

            if exists > 0:
                print(f"⚠️ 중복 건너뜀 - {row['ticket_title']}")
                continue

            # ✅ None 또는 NaN 처리
            def safe(val):
                return None if pd.isnull(val) or str(val).strip().lower() in ["없음", "nan"] else str(val)

            cursor.execute(insert_sql, (
                safe(row["ticket_title"]),
                safe(row["ticket_event_datetime"]),
                safe(row["ticket_price"]),
                safe(row.get("ticket_description", None)),
                safe(row["ticket_venue"]),
                safe(row["ticket_booking_link"]),
                safe(row["ticket_booking_provider"]),
                safe(row["ticket_booking_datetime"]),
                safe(row["ticket_created_at"]),
                safe(row["ticket_image_url"]),
                int(row["category_id"]) if not pd.isnull(row["category_id"]) else None
            ))
        except Exception as e:
            print(f"❌ INSERT 실패 - {row['ticket_title']}: {e}")

    conn.commit()
    cursor.close()
    conn.close()
    print("✅ MySQL INSERT 완료!")

if __name__ == "__main__":
    insert_to_mysql()
