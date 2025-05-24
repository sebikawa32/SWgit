package com.jose.ticket.domain.bookmark.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkCreateRequest {
    private Long userId;
    private Long ticketId;
}
