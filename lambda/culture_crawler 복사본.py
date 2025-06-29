import requests
import re
import time
import json
import pymysql
import boto3
from datetime import datetime, timedelta
from bs4 import BeautifulSoup
from urllib.parse import urlparse
from concurrent.futures import ThreadPoolExecutor, as_completed

API_KEY = "3936dca0-f7dd-4a2b-a06e-d503ea7b17bc"

CATEGORY_MAP = {
    "뮤지컬": 4, "연극": 3, "콘서트": 1, "전시": 2,
}

PROVIDER_KEYWORDS = {
    "interpark": "인터파크", "ticketlink": "티켓링크", "yes24": "예스24", "naver": "네이버예약",
    "lotteon": "롯데온", "sac.or.kr": "예술의전당", "ticket.yes24": "예스24티켓",
    "ticket.interpark": "인터파크티켓", "ticket.melon": "멜론티켓", "hancookticket": "한국티켓",
    "culture.go.kr": "문화포털", "busan.go.kr": "부산문화회관", "artgy.or.kr": "경기도문화의전당",
    "hanpac.or.kr": "한성백제문화회관", "museum.go.kr": "국립중앙박물관",
    "gyeongnam.go.kr": "경남도립미술관", "sbculture.or.kr": "성북구립미술관",
    "buyeo.museum.go.kr": "국립부여박물관"
}

def to_datetime_string(date_str):
    try:
        if date_str and len(date_str) == 8 and date_str.isdigit():
            return datetime.strptime(date_str, "%Y%m%d").strftime("%Y-%m-%d 00:00:00")
        elif "." in date_str:
            return datetime.strptime(date_str.replace(".", "-"), "%Y-%m-%d").strftime("%Y-%m-%d 00:00:00")
    except:
        return None

def get_provider_from_url(url):
    if not url:
        return "미정"
    host = urlparse(url).netloc.lower()
    for key, name in PROVIDER_KEYWORDS.items():
        if key in host:
            return name
    return "예매링크 참조"

def fetch_detail_info(detail_url, retries=2, delay=3):
    headers = {"User-Agent": "Mozilla/5.0"}
    for attempt in range(retries):
        try:
            res = requests.get(detail_url, headers=headers, timeout=4)
            res.encoding = 'utf-8'
            soup = BeautifulSoup(res.text, 'html.parser')
            board = soup.find('div', class_='board_detail')
            venue = price = book_link = genre_text = None
            if board:
                for dt, dd in zip(board.find_all('dt'), board.find_all('dd')):
                    label = dt.get_text(strip=True)
                    if label == "장소":
                        venue = dd.get_text(strip=True)
                    elif label == "요금":
                        price = dd.get_text(strip=True)
                    elif label == "바로가기":
                        a = dd.find('a')
                        book_link = a['href'] if a and a.has_attr('href') else None
                    elif label == "분야":
                        genre_text = dd.get_text(strip=True)
            return {
                "venue": venue, "price": price,
                "booking_link": book_link, "genre": genre_text
            }
        except:
            time.sleep(delay)
    return {}

def parse_item(item):
    title = item.get("title")
    period_raw = item.get("period")
    start_date, end_date = period_raw.split("~") if period_raw and "~" in period_raw else (None, None)
    start_date = to_datetime_string(start_date.strip()) if start_date else None
    end_raw = end_date.strip() if end_date else None
    try:
        end_obj = datetime.strptime(end_raw.replace(".", "-"), "%Y-%m-%d").date() if "." in end_raw else datetime.strptime(end_raw, "%Y%m%d").date()
        if end_obj < datetime.today().date():
            return None
    except:
        return None
    end_date = end_obj.strftime("%Y-%m-%d 00:00:00")
    link = item.get("url")
    desc_raw = item.get("description")
    description = re.sub(r"<.*?>", "", desc_raw or "")[:1000]
    detail = fetch_detail_info(link)
    venue, price = detail.get("venue", ""), detail.get("price", "")
    book_link = detail.get("booking_link") or link
    genre = detail.get("genre")
    provider = get_provider_from_url(book_link)
    image_url = item.get("imageObject", "")
    category_id = CATEGORY_MAP.get(genre, 1)
    return {
        "ticket_title": title, "ticket_event_start_datetime": start_date, "ticket_event_end_datetime": end_date,
        "ticket_price": price, "ticket_venue": venue, "ticket_booking_link": book_link,
        "ticket_booking_provider": provider, "ticket_created_at": datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
        "ticket_image_url": image_url, "ticket_description_url": image_url,
        "ticket_event_time": None, "ticket_age_limit": item.get("useTarget"),
        "category_id": category_id
    }

def crawl_culture_tickets():
    print("📡 KCISA API 요청 중...")
    url = "https://api.kcisa.kr/openapi/CNV_060/request"
    headers = {"Accept": "application/json", "User-Agent": "Mozilla/5.0"}
    params = {
        "serviceKey": API_KEY,
        "from": datetime.today().strftime("%Y%m%d"),
        "to": (datetime.today() + timedelta(days=90)).strftime("%Y%m%d")
    }
    res = requests.get(url, headers=headers, params=params)
    items = res.json().get("response", {}).get("body", {}).get("items")
    items = items.get("item", []) if isinstance(items, dict) else items
    if not isinstance(items, list): return []
    today_str = datetime.today().strftime("%Y%m%d")
    filtered = [item for item in items if "~" in item.get("period", "") and item.get("period").split("~")[1].strip().replace(".", "") >= today_str]
    print(f"🧹 필터링 후 남은 항목 수: {len(filtered)}")
    tickets = []
    count = 0
    with ThreadPoolExecutor(max_workers=15) as executor:
        futures = [executor.submit(parse_item, item) for item in filtered]
        for future in as_completed(futures):
            result = future.result()
            if result:
                tickets.append(result)
                count += 1
                if count % 50 == 0:
                    print(f"📦 현재 {count}개 수집 완료")
    print(f"📋 크롤링 완료: {len(tickets)}개")
    return tickets

def get_db_secret(secret_name, region_name="ap-northeast-2"):
    client = boto3.session.Session().client(service_name='secretsmanager', region_name=region_name)
    return json.loads(client.get_secret_value(SecretId=secret_name)['SecretString'])

def save_tickets_to_db(tickets, batch_size=200):
    print("🛠 DB 저장 시작...")
    secret = get_db_secret("my-db-secret")
    conn = pymysql.connect(
        host=secret["host"], user=secret["username"], password=secret["password"],
        db=secret["dbname"], port=int(secret["port"]), charset='utf8mb4'
    )
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
    with conn.cursor() as cursor:
        for i in range(0, len(tickets), batch_size):
            cursor.executemany(sql, tickets[i:i+batch_size])
            conn.commit()
    conn.close()
    print(f"✅ DB 저장 완료: {len(tickets)}개")

def lambda_handler(event=None, context=None):
    print("🚀 Lambda 시작")
    tickets = crawl_culture_tickets()
    save_tickets_to_db(tickets)
    print("🎉 Lambda 작업 완료")
    return {
        "statusCode": 200,
        "headers": {"Content-Type": "application/json; charset=utf-8"},
        "body": json.dumps({"message": f"{len(tickets)}개 저장 완료!"}, ensure_ascii=False)
    }
