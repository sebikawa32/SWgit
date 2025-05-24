package com.jose.ticket.domain.bookmark.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookmarkResponse {
    private Long bookmarkId;        //즐겨찾기고유 id
    private Long ticketId;         //티켓고유 id
    private String ticketTitle;     // 티켓 제목
    private LocalDateTime createdAt; // 북마크가 언제생성되었는지
}