import pymysql
import boto3
import os
import json

def get_db_secret(secret_name):
    """AWS Secrets Manager에서 DB 비밀 정보 가져오기 (리전 하드코딩)"""
    region_name = "ap-northeast-2"  # 원하는 리전으로 수정 가능
    session = boto3.session.Session()
    client = session.client(service_name='secretsmanager', region_name=region_name)
    get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    secret = get_secret_value_response['SecretString']
    return json.loads(secret)

def lambda_handler(event=None, context=None):
    # DB 시크릿 이름은 환경변수로 세팅
    secret_name = os.environ.get("DB_SECRET_NAME")
    if not secret_name:
        raise Exception("DB_SECRET_NAME 환경변수가 설정되지 않았습니다.")

    # DB 정보 받아오기
    db_secret = get_db_secret(secret_name)
    conn = pymysql.connect(
        host=db_secret['host'],
        user=db_secret['username'],
        password=db_secret['password'],
        db=db_secret['dbname'],
        port=int(db_secret.get('port', 3306)),
        charset='utf8mb4'
    )
    cur = conn.cursor()

    # 🔵 60일 지난 클릭 로그 먼저 삭제 (DB 공간 최적화)
    cur.execute("DELETE FROM ticket_click_log WHERE clicked_at < DATE_SUB(NOW(), INTERVAL 60 DAY)")
    conn.commit()  # 삭제 바로 반영

    # 티켓 아이디 전부 가져오기
    cur.execute("SELECT ticket_id FROM ticket")
    ticket_ids = [row[0] for row in cur.fetchall()]

    for ticket_id in ticket_ids:
        # 1. 즐겨찾기 수 (누적)
        cur.execute("SELECT COUNT(*) FROM bookmark WHERE ticket_id=%s", (ticket_id,))
        bookmark_count = cur.fetchone()[0]

        # 2. 예매 클릭 수 (최근 60일간)
        cur.execute("""
            SELECT COUNT(*) FROM ticket_click_log 
            WHERE ticket_id=%s AND clicked_at >= DATE_SUB(NOW(), INTERVAL 60 DAY)
        """, (ticket_id,))
        click_count = cur.fetchone()[0]

        # 3. 조회수 (누적)
        cur.execute("SELECT view_count FROM ticket WHERE ticket_id=%s", (ticket_id,))
        view_count = cur.fetchone()[0] or 0

        # 4. 인기도 점수 계산 (비율: 0.4, 0.5, 0.1)
        score = bookmark_count * 0.4 + click_count * 0.5 + view_count * 0.1

        # 5. ticket_popularity 테이블에 INSERT or UPDATE
        cur.execute("""
            INSERT INTO ticket_popularity (ticket_id, popularity_score)
            VALUES (%s, %s)
            ON DUPLICATE KEY UPDATE popularity_score=%s, updated_at=NOW()
        """, (ticket_id, score, score))

    conn.commit()
    cur.close()
    conn.close()
    print("티켓 인기도 계산 및 저장 완료!")

