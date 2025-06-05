from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
import pandas as pd
import time
from datetime import datetime
import os
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
                return dt_str  # 문자열 그대로 반환
    return "예매링크 참조"

def crawl_detail(detail_url, driver):
    try:
        driver.get(detail_url)
    except Exception as e:
        print(f"❌ driver.get() 실패: {detail_url} - {e}")
        return {
            "ticket_title": "없음",
            "ticket_event_datetime": "없음",
            "ticket_venue": "없음",
            "ticket_price": "없음",
            "ticket_image_url": "없음",
            "ticket_booking_link": detail_url,
            "ticket_booking_datetime": "예매링크 참조",
            "ticket_created_at": datetime.now()
        }

    try:
        ticket_title = "없음"
        try:
            WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, "h1.product_title, h2.product_title"))
            )
            title_tag = driver.find_element(By.CSS_SELECTOR, "h1.product_title, h2.product_title")
            ticket_title = title_tag.text.strip()
        except Exception:
            try:
                raw_title = driver.title
                ticket_title = raw_title.replace("- 티켓링크", "").strip()
            except:
                pass

        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "section.common_section.section_product_info"))
        )
        time.sleep(1)
        soup = BeautifulSoup(driver.page_source, "html.parser")

        ticket_event_datetime = "없음"
        ticket_venue = "없음"
        ticket_price = "없음"
        price_texts = []

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

        if price_texts:
            ticket_price = "; ".join(price_texts)

        image_tag = soup.select_one("img.product_detail_img")
        if image_tag and image_tag.has_attr("src"):
            src = image_tag["src"]
            ticket_image_url = "https:" + src if src.startswith("//") else src
        else:
            ticket_image_url = "없음"

        ticket_booking_datetime = extract_booking_datetime_from_text(soup)

        return {
            "ticket_title": ticket_title,
            "ticket_event_datetime": ticket_event_datetime,
            "ticket_venue": ticket_venue,
            "ticket_price": ticket_price,
            "ticket_image_url": ticket_image_url,
            "ticket_booking_link": detail_url,
            "ticket_booking_datetime": ticket_booking_datetime,
            "ticket_created_at": datetime.now()
        }

    except Exception as e:
        print(f"❌ 상세 페이지 크롤링 실패: {detail_url} - {e}")
        return {
            "ticket_title": "없음",
            "ticket_event_datetime": "없음",
            "ticket_venue": "없음",
            "ticket_price": "없음",
            "ticket_image_url": "없음",
            "ticket_booking_link": detail_url,
            "ticket_booking_datetime": "예매링크 참조",
            "ticket_created_at": datetime.now()
        }

def main():
    df = pd.read_csv("data/ticketlink_list.csv")
    detail_results = []

    options = Options()
    options.add_argument("--headless")
    options.add_argument("--disable-gpu")
    options.add_argument("--no-sandbox")
    options.add_argument("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")

    driver = webdriver.Chrome(options=options)

    for idx, row in df.iterrows():
        detail_url = row["ticket_booking_link"]

        if "bridge" in detail_url:
            print(f"⛔ bridge URL 제외됨: {detail_url}")
            continue

        if idx > 0 and idx % 10 == 0:
            driver.quit()
            time.sleep(1)
            driver = webdriver.Chrome(options=options)

        if detail_url != "없음" and "http" in detail_url:
            print(f"상세 크롤링 중: {detail_url}")
            detail_data = crawl_detail(detail_url, driver)
            detail_data["category_id"] = row["category_id"]
            detail_results.append(detail_data)
            time.sleep(0.5)
        else:
            detail_results.append({
                "ticket_title": row["ticket_title"],
                "ticket_event_datetime": "예매링크 확인",
                "ticket_venue": "예매링크 확인",
                "ticket_price": "예매링크 확인",
                "ticket_image_url": "예매링크 확인",
                "ticket_booking_link": detail_url,
                "ticket_booking_datetime": "예매링크 참조",
                "ticket_created_at": datetime.now(),
                "category_id": row["category_id"]
            })

    driver.quit()

    detail_df = pd.DataFrame(detail_results)
    os.makedirs("data", exist_ok=True)
    detail_df.to_csv("data/ticketlink_detail.csv", index=False, encoding="utf-8-sig")
    print("✅ 상세 크롤링 완료! data/ticketlink_detail.csv 파일을 확인하세요.")

if __name__ == "__main__":
    main()