package com.jose.ticket.domain.ticketinfo.dto;

import java.time.LocalDateTime;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;

import lombok.Getter;

/***********목록에 보일 정보를 담은 DTO 입니다 ******************/

@Getter
public class TicketResponseDto {
    private final Long id;
    private final String title;
    private final String venue;
    private final LocalDateTime eventDatetime;
    private final String imageUrl;

    // Entity → DTO 변환용 생성자
    public TicketResponseDto(TicketEntity ticket) {
        this.id = ticket.getId();
        this.title = ticket.getTitle(); // ticket_title
        this.venue = ticket.getVenue(); // ticket_venue
        this.eventDatetime = ticket.getEventDatetime(); // ticket_event_datetime
        this.imageUrl = ticket.getImageUrl(); // ticket_image_url
    }


}
