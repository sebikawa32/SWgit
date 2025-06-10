package com.jose.ticket.domain.search.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDto {
    private String query;
    private Integer categoryId;

    public SearchRequestDto(String query, Integer categoryId) {
        this.query = query;
        this.categoryId = categoryId;
    }

    // ✅ 기본 생성자도 꼭 필요함 (스프링이 내부적으로 사용함)
    public SearchRequestDto() {
    }
}

