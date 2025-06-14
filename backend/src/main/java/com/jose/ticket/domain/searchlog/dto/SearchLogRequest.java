package com.jose.ticket.domain.searchlog.dto;

import lombok.Data;

@Data
public class SearchLogRequest {
    private Long userId;            // optional
    private String keyword;         // 사용자가 입력한 원본 검색어
}
