import os
import pymysql
import boto3
import json
from datetime import datetime, timedelta

def get_db_secret(secret_name, region_name="ap-northeast-2"):
    session = boto3.session.Session()
    client = session.client(service_name='secretsmanager', region_name=region_name)
    get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    secret = get_secret_value_response['SecretString']
    return json.loads(secret)

def delete_expired_tickets():
    # 오늘 기준 이틀 전까지 끝난 공연 삭제 (오늘 6/10이면 6/8까지 삭제)
    today = datetime.now()
    target_date = today - timedelta(days=2)
    target_date_str = target_date.strftime('%Y-%m-%d')
    print(f"[INFO] Deleting tickets ended before or on: {target_date_str}")

    # DB 비밀 정보 받아오기 (람다 환경변수로 등록된 이름 사용)
    secret_name = os.environ.get("DB_SECRET_NAME")
    if not secret_name:
        print("[ERROR] DB_SECRET_NAME 환경변수가 설정되어 있지 않음")
        return 0

    db_secret = get_db_secret(secret_name)

    conn = pymysql.connect(
        host=db_secret['host'],
        user=db_secret['username'],
        password=db_secret['password'],
        db=db_secret['dbname'],
        port=int(db_secret.get('port', 3306)),
        charset='utf8mb4'
    )
    try:
        with conn.cursor() as cursor:
            delete_sql = """
                DELETE FROM ticket
                WHERE DATE(ticket_event_end_datetime) <= %s
            """
            cursor.execute(delete_sql, (target_date_str,))
            deleted_count = cursor.rowcount
            print(f"[INFO] {deleted_count} rows deleted")
        conn.commit()
        return deleted_count
    finally:
        conn.close()

def lambda_handler(event, context):
    print("[INFO] Lambda function started")
    deleted = delete_expired_tickets()
    print(f"[INFO] Lambda finished. Expired tickets deleted: {deleted}")
    return {
        "statusCode": 200,
        "body": f"Expired tickets deleted: {deleted}"
    }
