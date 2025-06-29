import pymysql
import boto3
import os
import json
from datetime import datetime

def get_db_secret(secret_name, region_name="ap-northeast-2"):
    session = boto3.session.Session()
    client = session.client(service_name='secretsmanager', region_name=region_name)
    secret_value = client.get_secret_value(SecretId=secret_name)
    secret = secret_value['SecretString']
    return json.loads(secret)

def get_connection(db_secret):
    return pymysql.connect(
        host=db_secret["host"],
        user=db_secret["username"],
        password=db_secret["password"],
        database=db_secret["dbname"],
        port=int(db_secret.get("port", 3306)),
        charset='utf8mb4',
        cursorclass=pymysql.cursors.DictCursor
    )

def lambda_handler(event, context):
    secret_name = os.environ.get("DB_SECRET_NAME")
    if not secret_name:
        raise Exception("DB_SECRET_NAME 환경변수가 설정되지 않았습니다.")

    db_secret = get_db_secret(secret_name)
    conn = get_connection(db_secret)

    deleted_count = 0
    try:
        with conn.cursor() as cursor:
            # 오늘 날짜 (00:00:00 기준)
            today = datetime.now().strftime('%Y-%m-%d')

            # 공연 종료일이 오늘보다 이전인 티켓 삭제
            sql = "DELETE FROM ticket WHERE ticket_event_end_datetime < %s"
            cursor.execute(sql, (today,))
            deleted_count = cursor.rowcount

        conn.commit()
    finally:
        conn.close()

    return {
        "statusCode": 200,
        "body": f"{deleted_count}개의 만료된 티켓이 삭제되었습니다."
    }
