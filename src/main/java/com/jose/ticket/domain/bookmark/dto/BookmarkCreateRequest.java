package com.jose.ticket.domain.bookmark.dto;

import lombok.Getter;
import lombok.Setter;

// 즐겨찾기 추가 요청 시 전달되는 데이터 모델
@Getter
@Setter
public class BookmarkCreateRequest {
    private Long userId;   // 사용자 ID
    private Long ticketId; // 티켓 ID
}
