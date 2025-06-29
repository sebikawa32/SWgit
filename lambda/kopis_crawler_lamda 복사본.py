import requests
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta
import time
import pymysql
import boto3
import json
import re
from concurrent.futures import ThreadPoolExecutor, as_completed

# KOPIS OpenAPI 서비스키 (네 키로 바꿔서 쓰면 됨)
service_key = 'fbe0cb03591248b3b43a3dd78a8be578'

# 장르명 → category_id 매핑
genre_map = {
    '뮤지컬': 4,
    '연극': 3,
    '서양음악(클래식)': 1,
    '대중음악': 1,
    '한국음악(국악)': 1,
    '무용': 1,
    '대중무용': 1,
    '서커스/마술': 1,
    '복합': 1,
}

# 괄호 및 괄호 안 내용 제거 (공연장 이름 전처리)
def remove_parentheses(text):
    if text:
        # (가 나오기 전까지만 남김
        return text.split('(')[0].strip()
    return text


# 예매처, 예매링크 파싱
def get_booking_info(data):
    relates = data.find('relates')
    if relates is not None:
        relate = relates.find('relate')
        if relate is not None:
            provider = relate.findtext('relatenm')
            link = relate.findtext('relateurl')
            return link, provider
    return None, None

# yyyy.mm.dd → yyyy-mm-dd로 변환
def format_date(raw_date):
    try:
        return datetime.strptime(raw_date, '%Y.%m.%d').strftime('%Y-%m-%d')
    except:
        return None

# 공연 시간 정보 파싱
def get_event_time(data):
    return data.findtext('dtguidance') or None

# 상세 설명 이미지 URL 여러개 연결 (최대 500자)
def get_description_url(data):
    styurls = data.find('styurls')
    if styurls is not None:
        urls = [styurl.text for styurl in styurls.findall('styurl') if styurl.text]
        return ';'.join(urls)[:500] if urls else None
    return None

# AWS Secrets Manager에서 DB 정보 가져오기
def get_db_secret(secret_name, region_name="ap-northeast-2"):
    session = boto3.session.Session()
    client = session.client(service_name='secretsmanager', region_name=region_name)
    get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    secret = get_secret_value_response['SecretString']
    return json.loads(secret)

# 공연 상세 데이터 크롤링 (멀티스레딩용)
def fetch_detail(prfid):
    try:
        detail_url = f'http://www.kopis.or.kr/openApi/restful/pblprfr/{prfid}?service={service_key}'
        dres = requests.get(detail_url, timeout=10)
        dres.encoding = 'utf-8'
        data = ET.fromstring(dres.text).find('db')
        if data is not None:
            ticket_booking_link, ticket_booking_provider = get_booking_info(data)
            genre_name = data.findtext('genrenm')
            category_id = genre_map.get(genre_name, 0)
            start_date = data.findtext('prfpdfrom')
            end_date_ = data.findtext('prfpdto')
            ticket = {
                'ticket_title': data.findtext('prfnm') or None,
                'ticket_price': data.findtext('pcseguidance') or None,
                # 공연장명에 괄호 제거!
                'ticket_venue': remove_parentheses(data.findtext('fcltynm')) or None,
                'ticket_booking_link': ticket_booking_link,
                'ticket_booking_provider': ticket_booking_provider,
                'ticket_created_at': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                'ticket_image_url': data.findtext('poster') or None,
                'category_id': category_id,
                'ticket_event_start_datetime': format_date(start_date) if start_date else None,
                'ticket_event_end_datetime': format_date(end_date_) if end_date_ else None,
                'ticket_event_time': get_event_time(data),
                'ticket_description_url': get_description_url(data),
                'ticket_age_limit': data.findtext('prfage') or None
            }
            print('수집:', ticket['ticket_title'])
            return ticket
        else:
            return None
    except Exception as e:
        print(f"상세 요청 에러: {prfid}, {e}")
        return None

# 전체 공연ID 긁고 → 상세 크롤링(스레드로 빠르게)
def crawl_all_tickets():
    today = datetime.today()
    start_date_str = today.strftime('%Y%m%d')
    end_date = today + timedelta(days=730)
    end_date_str = end_date.strftime('%Y%m%d')

    show_ids = []
    page = 1
    rows = 100

    # 페이지별로 공연ID 수집
    while True:
        list_url = (
            f'http://www.kopis.or.kr/openApi/restful/pblprfr?service={service_key}'
            f'&stdate={start_date_str}&eddate={end_date_str}'
            f'&cpage={page}&rows={rows}'
        )
        res = requests.get(list_url)
        res.encoding = 'utf-8'
        root = ET.fromstring(res.text)
        items = root.findall('db')
        if not items:
            break
        for item in items:
            show_id = item.findtext('mt20id')
            if show_id:
                show_ids.append(show_id)
        print(f'페이지 {page} 완료, 누적 {len(show_ids)}개')
        page += 1
        time.sleep(0.05)  # 목록은 빠르게!

    print(f'공연 ID 전체 개수: {len(show_ids)}')

    # 공연 상세 15스레드로 병렬 크롤링
    tickets = []
    with ThreadPoolExecutor(max_workers=15) as executor:
        futures = [executor.submit(fetch_detail, prfid) for prfid in show_ids]
        for future in as_completed(futures):
            ticket = future.result()
            if ticket:
                tickets.append(ticket)

    print(f'수집 완료: {len(tickets)}개')
    return tickets

# DB batch insert로 빠르게 저장
def save_tickets_to_db(tickets, batch_size=200):
    secret_dict = get_db_secret("my-db-secret")
    conn = pymysql.connect(
        host=secret_dict["host"],
        user=secret_dict["username"],
        password=secret_dict["password"],
        db=secret_dict["dbname"],
        port=int(secret_dict["port"]),
        charset='utf8mb4'
    )
    cursor = conn.cursor()
    sql = """
    INSERT INTO ticket (
        ticket_title, ticket_price, ticket_venue, ticket_booking_link, ticket_booking_provider,
        ticket_created_at, ticket_image_url, category_id, ticket_event_start_datetime, ticket_event_end_datetime,
        ticket_event_time, ticket_description_url, ticket_age_limit
    ) VALUES (
        %(ticket_title)s, %(ticket_price)s, %(ticket_venue)s, %(ticket_booking_link)s, %(ticket_booking_provider)s,
        %(ticket_created_at)s, %(ticket_image_url)s, %(category_id)s, %(ticket_event_start_datetime)s, %(ticket_event_end_datetime)s,
        %(ticket_event_time)s, %(ticket_description_url)s, %(ticket_age_limit)s
    )
    ON DUPLICATE KEY UPDATE
        ticket_title=VALUES(ticket_title),
        ticket_price=VALUES(ticket_price),
        ticket_venue=VALUES(ticket_venue),
        ticket_booking_provider=VALUES(ticket_booking_provider),
        ticket_created_at=VALUES(ticket_created_at),
        ticket_image_url=VALUES(ticket_image_url),
        category_id=VALUES(category_id),
        ticket_event_start_datetime=VALUES(ticket_event_start_datetime),
        ticket_event_end_datetime=VALUES(ticket_event_end_datetime),
        ticket_event_time=VALUES(ticket_event_time),
        ticket_description_url=VALUES(ticket_description_url),
        ticket_age_limit=VALUES(ticket_age_limit)
    """
    total = 0
    for i in range(0, len(tickets), batch_size):
        batch = tickets[i:i+batch_size]
        cursor.executemany(sql, batch)
        total += len(batch)
        print(f"DB 저장 진행: {total} / {len(tickets)}")

    conn.commit()
    cursor.close()
    conn.close()
    print(f"DB 저장 완료 ({len(tickets)}개)")

# AWS Lambda 엔트리포인트 (실행 시 여기서 전체 동작 시작)
def lambda_handler(event=None, context=None):
    tickets = crawl_all_tickets()
    save_tickets_to_db(tickets)
    return {
        "statusCode": 200,
        "body": f"{len(tickets)}개 저장 완료!"
    }
