package com.jose.ticket.domain.ticketinfo.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class TicketDetailResponseDto {
    private String imageUrl;
    private String title;
    private LocalDateTime eventDatetime;
    private Integer price;
    private String description;
    private String venue;
    private String bookingLink;
    private String bookingProvider;
    private LocalDateTime bookingDatetime;

    public TicketDetailResponseDto(String imageUrl, String title, LocalDateTime eventDatetime,
                                   Integer price, String description, String venue,
                                   String bookingLink, String bookingProvider, LocalDateTime bookingDatetime) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.eventDatetime = eventDatetime;
        this.price = price;
        this.description = description;
        this.venue = venue;
        this.bookingLink = bookingLink;
        this.bookingProvider = bookingProvider;
        this.bookingDatetime = bookingDatetime;
    }
}
