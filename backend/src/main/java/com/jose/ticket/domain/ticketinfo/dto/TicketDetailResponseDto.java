package com.jose.ticket.domain.ticketinfo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;

import lombok.Getter;

@Getter
public class TicketDetailResponseDto {
    private String imageUrl;
    private String title;
    private LocalDateTime eventStartDatetime;
    private LocalDateTime eventEndDatetime;
    private String price;
    private String venue;
    private String bookingLink;
    private String bookingProvider;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime bookingDatetime;

    private String categoryName;
    private String ageLimit;
    private String eventTime;

    // 수정: String → List<String>
    private List<String> descriptionUrl;

    // Entity 기반 생성자
    public TicketDetailResponseDto(TicketEntity ticket, String categoryName) {
        this.imageUrl = ticket.getImageUrl();
        this.title = ticket.getTitle();
        this.eventStartDatetime = ticket.getEventStartDatetime();
        this.eventEndDatetime = ticket.getEventEndDatetime();
        this.price = ticket.getPrice();
        this.venue = ticket.getVenue();
        this.bookingLink = ticket.getBookingLink();
        this.bookingProvider = ticket.getBookingProvider();
        this.bookingDatetime = ticket.getBookingDatetime();
        this.categoryName = categoryName;
        this.ageLimit = ticket.getAgeLimit();
        this.eventTime = ticket.getEventTime();

        // JSON 문자열 or 단일 문자열 모두 처리
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> urls = new ArrayList<>();
        try {
            String raw = ticket.getDescriptionUrl();
            if (raw != null) {
                raw = raw.trim();
                if (raw.startsWith("[")) {
                    // JSON 배열
                    urls = objectMapper.readValue(raw, new TypeReference<List<String>>() {});
                } else {
                    // 단일 문자열
                    urls.add(raw);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 필요시 로깅
        }
        this.descriptionUrl = urls;
    }
}
