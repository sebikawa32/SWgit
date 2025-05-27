package com.jose.ticket.domain.search.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDto {
    private String query;
    private Long categoryId;
}
