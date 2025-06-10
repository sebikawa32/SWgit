from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
import time
import re

from detail_crawler import crawl_detail
from db_insert import insert_to_mysql

def main():
    options = Options()
    options.add_argument("--headless")
    options.add_argument("--disable-gpu")
    options.add_argument("--no-sandbox")
    options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    driver = webdriver.Chrome(options=options)

    categories = {
        "연극": ("https://www.ticketlink.co.kr/performance/15", 3),
        "뮤지컬": ("https://www.ticketlink.co.kr/performance/16", 2),
        "콘서트": ("https://www.ticketlink.co.kr/performance/14", 1),
        "전시": ("https://www.ticketlink.co.kr/exhibition/11", 4)
    }

    for category_name, (url, category_id) in categories.items():
        print(f"\n▶ 카테고리: {category_name}")
        driver.get(url)

        try:
            WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, "ul.product_grid_list > li.product_grid_item"))
            )
        except Exception as e:
            print(f"❌ 공연 목록 로딩 실패: {url} | 에러: {e}")
            continue

        soup = BeautifulSoup(driver.page_source, 'html.parser')
        performance_list = soup.select('ul.product_grid_list > li.product_grid_item')
        print("공연 개수:", len(performance_list))

        for perf in performance_list:
            try:
                # 공연 제목
                title_tag = perf.select_one('.product_title')
                title = title_tag.get_text(strip=True) if title_tag else "제목 없음"

                # 링크 추출
                link_tag = perf.select_one('a')
                if link_tag and link_tag.has_attr('href'):
                    href = link_tag['href']
                    detail_url = href if href.startswith("http") else "https://www.ticketlink.co.kr" + href
                else:
                    onclick_attr = perf.get("onclick")
                    if onclick_attr and "location.href" in onclick_attr:
                        href_match = re.search(r"location\.href\s*=\s*[\"']([^\"']+)[\"']", onclick_attr)
                        if href_match:
                            href = href_match.group(1)
                            detail_url = "https://www.ticketlink.co.kr" + href
                        else:
                            print("❌ onclick에서 href 추출 실패")
                            continue
                    else:
                        print("❌ 링크 없음, 건너뜀")
                        continue

                # 상세 페이지 → 크롤링
                detail_data = crawl_detail(detail_url, driver)
                detail_data["category_id"] = category_id
                insert_to_mysql(detail_data)

                time.sleep(0.5)  # 서버 부담 방지

            except Exception as e:
                print(f"❌ 하나의 공연 처리 중 에러 발생: {e}")

    driver.quit()
    print("\n✅ 전체 크롤링 및 DB 저장 완료!")

if __name__ == "__main__":
    main()
