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
    private LocalDateTime createdAt; // 즐겨찾기 생성 시각
}
