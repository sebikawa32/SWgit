from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
from datetime import datetime
import time
import re


def extract_booking_datetime_from_text(soup):
    paragraphs = soup.select("div.product_editor p")
    for p in paragraphs:
        text = p.get_text(strip=True)
        if "티켓오픈" in text or "티켓 오픈" in text:
            match = re.search(r"(\d{4})년\s*(\d{2})월\s*(\d{2})일.*?(오전|오후)?\s*(\d{1,2})시", text)
            if match:
                year, month, day, ampm, hour = match.groups()
                hour = int(hour)
                if ampm == "오후" and hour != 12:
                    hour += 12
                if ampm == "오전" and hour == 12:
                    hour = 0
                dt_str = f"{year}-{month}-{day} {hour:02d}:00:00"
                return dt_str
    return "예매링크 참조"


def parse_event_period(text):
    match = re.match(r"(\d{4}\.\d{2}\.\d{2})\s*[-~]\s*(\d{4}\.\d{2}\.\d{2})", text)
    if match:
        start_str, end_str = match.groups()
        try:
            start_dt = datetime.strptime(start_str, "%Y.%m.%d")
            end_dt = datetime.strptime(end_str, "%Y.%m.%d")
            return start_dt, end_dt
        except:
            pass
    return None, None


def crawl_detail(detail_url, driver):
    try:
        driver.get(detail_url)
    except Exception as e:
        print(f"❌ driver.get() 실패: {detail_url} - {e}")
        return default_result(detail_url, category_id=None)

    try:
        # 초기화
        ticket_title = "없음"
        ticket_price = "없음"
        ticket_venue = "없음"
        ticket_image_url = "없음"
        ticket_age_limit = "없음"
        ticket_event_time = "없음"
        ticket_description_url = "없음"
        price_texts = []

        # 제목
        try:
            WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, "h1.product_title, h2.product_title"))
            )
            title_tag = driver.find_element(By.CSS_SELECTOR, "h1.product_title, h2.product_title")
            ticket_title = title_tag.text.strip()
        except:
            try:
                ticket_title = driver.title.replace("- 티켓링크", "").strip()
            except:
                pass

        # 주요 정보 영역 로딩
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "section.common_section.section_product_info"))
        )
        time.sleep(1)
        soup = BeautifulSoup(driver.page_source, "html.parser")

        ticket_event_datetime = "없음"
        info_items = soup.select("li.product_info_item")

        for item in info_items:
            label = item.select_one(".product_info_title")
            desc = item.select_one(".product_info_desc")
            if not label or not desc:
                continue

            label_text = label.get_text(strip=True)

            if "기간" in label_text:
                ticket_event_datetime = desc.get_text(strip=True)
            elif "장소" in label_text:
                ticket_venue = desc.get_text(strip=True)
            elif "가격" in label_text:
                for li in desc.select("li"):
                    price_texts.append(li.get_text(strip=True))
                if not price_texts:
                    price_texts.append(desc.get_text(strip=True))
            elif "관람" in label_text or "연령" in label_text:
                ticket_age_limit = desc.get_text(strip=True)

        if price_texts:
            ticket_price = "; ".join(price_texts)

        # 썸네일 이미지
        image_tag = soup.select_one("img.product_detail_img")
        if image_tag and image_tag.has_attr("src"):
            src = image_tag["src"]
            ticket_image_url = "https:" + src if src.startswith("//") else src

        # 상세설명 이미지 (ticket_description_url)
        description_image = soup.select_one("section#product_tab .product_editor img")
        if description_image and description_image.has_attr("src"):
            ticket_description_url = description_image["src"]
            if ticket_description_url.startswith("//"):
                ticket_description_url = "https:" + ticket_description_url

        # 공연시간 정보 추출
        event_time_section = soup.select("section#product_tab .product_editor")
        for section in event_time_section:
            subtitle = section.select_one("h3.product_content_subtit")
            if subtitle and "공연시간" in subtitle.get_text():
                time_paragraphs = section.select("p")
                time_texts = [p.get_text(strip=True) for p in time_paragraphs if p.get_text(strip=True)]
                ticket_event_time = "\n".join(time_texts)
                break

        # 예매 시작일
        ticket_booking_datetime = extract_booking_datetime_from_text(soup)

        # 공연 기간 (시작/종료일)
        event_start_dt, event_end_dt = parse_event_period(ticket_event_datetime)

        return {
            "ticket_title": ticket_title,
            "ticket_price": ticket_price,
            "ticket_description": "없음",
            "ticket_venue": ticket_venue,
            "ticket_booking_link": detail_url,
            "ticket_booking_provider": "티켓링크",
            "ticket_booking_datetime": ticket_booking_datetime,
            "ticket_created_at": datetime.now(),
            "ticket_image_url": ticket_image_url,
            "category_id": None,
            "ticket_event_start_datetime": event_start_dt,
            "ticket_event_end_datetime": event_end_dt,
            "ticket_age_limit": ticket_age_limit,
            "ticket_event_time": ticket_event_time,
            "ticket_description_url": ticket_description_url
        }

    except Exception as e:
        print(f"❌ 상세 페이지 크롤링 실패: {detail_url} - {e}")
        return default_result(detail_url, category_id=None)


def default_result(detail_url, category_id):
    return {
        "ticket_title": "없음",
        "ticket_price": "없음",
        "ticket_description": "없음",
        "ticket_venue": "없음",
        "ticket_age_limit": "없음",
        "ticket_booking_link": detail_url,
        "ticket_booking_provider": "티켓링크",
        "ticket_booking_datetime": None,
        "ticket_created_at": datetime.now(),
        "ticket_image_url": "없음",
        "category_id": category_id,
        "ticket_event_start_datetime": None,
        "ticket_event_end_datetime": None,
        "ticket_event_time": "없음",
        "ticket_description_url": "없음"
    }
