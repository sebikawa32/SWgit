package com.jose.ticket.domain.ticketinfo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ticket")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(name = "ticket_title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_category", nullable = false)
    private TicketCategory category;

    @Column(name = "ticket_event_datetime", nullable = false)
    private LocalDateTime eventDatetime;

    @Column(name = "ticket_price")
    private Integer price;

    @Column(name = "ticket_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ticket_venue")
    private String venue;

    @Column(name = "ticket_booking_link", length = 500)
    private String bookingLink;

    @Column(name = "ticket_booking_provider", length = 100)
    private String bookingProvider;

    @Column(name = "ticket_booking_datetime")
    private LocalDateTime bookingDatetime;

    @Column(name = "ticket_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ticket_image_url", length = 1000)
    private String imageUrl;

    // Enum 정의는 엔티티 안이나 별도 파일에 만들어도
    public enum TicketCategory {
        concert, exhibition, theaterPlay, musical
    }

}

