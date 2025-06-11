package com.jose.ticket.domain.bookmark.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 즐겨찾기 삭제 요청 시 전달되는 데이터 모델
@Getter
@Setter
@NoArgsConstructor
public class BookmarkDeleteRequest {
    private Long ticketId;   // 프론트에서 받음
    private Long userId;     // 백엔드에서 setUserId()로 주입됨
}
