package com.jose.ticket.domain.keyword.dto;

import lombok.Data;

@Data
public class PopularKeywordResponse {
    private String keyword;
    private int count;
}
