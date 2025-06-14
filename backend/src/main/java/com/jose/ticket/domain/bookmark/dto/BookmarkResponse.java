package com.jose.ticket.domain.bookmark.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookmarkResponse {
    private Long bookmarkId;        // 즐겨찾기 고유 ID
    private Long ticketId;          // 티켓 고유 ID
    private String ticketTitle;     // 티켓 제목
    private String venue;           // 공연장
    private LocalDateTime eventDatetime; // 공연 날짜시간
    private String imageUrl;        // 공연 이미지 URL
    private String categoryName;     //추가된 카테고리 이름
    private LocalDateTime createdAt; // 즐겨찾기 생성 시각
    private LocalDateTime eventStartDatetime; // ← 추가
    private LocalDateTime eventEndDatetime;   // ← 추가
}