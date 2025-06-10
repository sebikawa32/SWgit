package com.jose.ticket.domain.ticketinfo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/******* 요청 DTO 입니다. *******/

@Getter
@Setter
public class TicketRequestDto {
    private String title;
    private Long categoryId; // 클라이언트에서 보낼 때는 category의 ID만!
    private LocalDateTime eventStartDatetime; // 시작일
    private LocalDateTime eventEndDatetime;   // 종료일
    private String price;
    private String venue;
    private String bookingLink;
    private String bookingProvider;

    // ✅ 타입 변경 및 포맷 지정
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime bookingDatetime;

    private String imageUrl;
}
