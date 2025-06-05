import pandas as pd
from datetime import datetime
import os

def merge_ticket_data():
    list_df = pd.read_csv("data/ticketlink_list.csv")
    detail_df = pd.read_csv("data/ticketlink_detail.csv")

    list_df.columns = list_df.columns.str.strip()
    detail_df.columns = detail_df.columns.str.strip()

    print("📋 list_df 컬럼:", list_df.columns.tolist())
    print("📋 detail_df 컬럼:", detail_df.columns.tolist())

    merged_df = pd.merge(
        list_df,
        detail_df,
        on="ticket_booking_link",
        how="left",
        suffixes=('', '_dup')
    )

    merged_df["ticket_booking_provider"] = "티켓링크"
    merged_df["ticket_booking_datetime"] = merged_df["ticket_booking_datetime"].fillna("예매링크 참조")

    merged_df = merged_df[[
        "ticket_title",
        "ticket_event_datetime",
        "ticket_price",
        "ticket_venue",
        "ticket_booking_link",
        "ticket_booking_provider",
        "ticket_booking_datetime",
        "ticket_created_at",
        "ticket_image_url",
        "category_id"
    ]]

    os.makedirs("data", exist_ok=True)
    merged_df.to_csv("data/merged_ticket_data.csv", index=False, encoding="utf-8-sig")
    print("✅ CSV 병합 완료! data/merged_ticket_data.csv 확인해보세요.")

if __name__ == "__main__":
    merge_ticket_data()
