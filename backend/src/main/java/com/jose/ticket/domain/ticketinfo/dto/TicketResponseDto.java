package com.jose.ticket.domain.ticketinfo.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;

import lombok.Getter;

/*********** 목록 응답 DTO 입니다 ******************/

@Getter
public class TicketResponseDto {
    private final Long id;
    private final String title;
    private final String venue;
    private final LocalDateTime eventStartDatetime; // 공연 시작일
    private final LocalDateTime eventEndDatetime;   // 공연 종료일
    private final String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDateTime bookingDatetime;

    private final Long daysUntilDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;  //  추가된 필드

    // Entity → DTO 변환용 생성자
    public TicketResponseDto(TicketEntity ticket) {
        this.id = ticket.getId();
        this.title = ticket.getTitle();
        this.venue = ticket.getVenue();
        this.eventStartDatetime = ticket.getEventStartDatetime();
        this.eventEndDatetime = ticket.getEventEndDatetime();
        this.imageUrl = ticket.getImageUrl();
        this.bookingDatetime = ticket.getBookingDatetime();

        Long daysLeft = null;
        if (ticket.getEventStartDatetime() != null) {
            daysLeft = Duration.between(LocalDateTime.now(), ticket.getEventStartDatetime()).toDays();
        }
        this.daysUntilDeadline = daysLeft;

        this.createdAt = ticket.getCreatedAt();  //  생성자에 필드 추가
    }
}
