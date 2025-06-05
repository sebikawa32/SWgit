package com.jose.ticket.domain.ticketinfo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class TicketDetailResponseDto {
    private String imageUrl;
    private String title;
    private LocalDateTime eventStartDatetime; // 공연 시작일
    private LocalDateTime eventEndDatetime;   // 공연 종료일
    private String price;
    private String description;
    private String venue;
    private String bookingLink;
    private String bookingProvider;

    // ✅ 타입 변경 + 포맷/Null 처리
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime bookingDatetime;

    private String categoryName;  // ✅ 새 필드 추가

    // ✅ 생성자 수정 - categoryName 파라미터 추가
    public TicketDetailResponseDto(String imageUrl,
                                   String title,
                                   LocalDateTime eventStartDatetime,
                                   LocalDateTime eventEndDatetime,
                                   String price,
                                   String description,
                                   String venue,
                                   String bookingLink,
                                   String bookingProvider,
                                   LocalDateTime bookingDatetime,
                                   String categoryName) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.eventStartDatetime = eventStartDatetime;
        this.eventEndDatetime = eventEndDatetime;
        this.price = price;
        this.description = description;
        this.venue = venue;
        this.bookingLink = bookingLink;
        this.bookingProvider = bookingProvider;
        this.bookingDatetime = bookingDatetime;
        this.categoryName = categoryName;  // 생성자에서 초기화
    }
}

