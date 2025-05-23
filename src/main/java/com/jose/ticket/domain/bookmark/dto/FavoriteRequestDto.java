package com.jose.ticket.domain.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRequestDto {
    private Long userId;     // 즐겨찾기한 사용자 ID
    private Long ticketId;   // 즐겨찾기 대상 티켓 ID
}
