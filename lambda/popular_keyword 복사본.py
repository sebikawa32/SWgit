import pymysql
import boto3
import os
from datetime import datetime, timedelta
import json

def get_db_secret(secret_name):
    region_name = "ap-northeast-2"
    session = boto3.session.Session()
    client = session.client(service_name='secretsmanager', region_name=region_name)
    get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    secret = get_secret_value_response['SecretString']
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

def lambda_handler(event=None, context=None):
    secret_name = os.environ.get("DB_SECRET_NAME")
    if not secret_name:
        raise Exception("DB_SECRET_NAME 환경변수가 설정되지 않았습니다.")

    db_secret = get_db_secret(secret_name)

    DAYS_AGGREGATE = 3  # 집계 기준 (3일)
    DAYS_DELETE = 4     # 삭제 기준 (4일)
    LIMIT = 10          # 인기검색어 TOP 10

    now = datetime.now()
    # 집계 기준: 오늘 - 3일
    since_aggregate = now - timedelta(days=DAYS_AGGREGATE)
    # 삭제 기준: 오늘 - 4일
    before_delete = now - timedelta(days=DAYS_DELETE)

    conn = get_connection(db_secret)
    deleted_count = 0
    try:
        with conn.cursor() as cursor:
            # 1. 4일 지난 로그 삭제
            cursor.execute(
                "DELETE FROM search_log WHERE searched_at < %s",
                (before_delete,)
            )
            deleted_count = cursor.rowcount

            # 2. 최근 3일치 집계
            cursor.execute("""
                SELECT normalized_keyword, COUNT(*) AS cnt
                FROM search_log
                WHERE searched_at >= %s
                GROUP BY normalized_keyword
                ORDER BY cnt DESC
                LIMIT %s
            """, (since_aggregate, LIMIT))
            results = cursor.fetchall()

            # 3. 인기검색어 테이블 upsert
            for row in results:
                keyword = row['normalized_keyword']
                count = row['cnt']
                cursor.execute("""
                    INSERT INTO popular_keyword (normalized_keyword, count, updated_at)
                    VALUES (%s, %s, NOW())
                    ON DUPLICATE KEY UPDATE count=%s, updated_at=NOW()
                """, (keyword, count, count))
        conn.commit()
    finally:
        conn.close()

    return {
        "status": "ok",
        "deleted_logs": deleted_count,
        "updated_keywords": [row['normalized_keyword'] for row in results]
    }
