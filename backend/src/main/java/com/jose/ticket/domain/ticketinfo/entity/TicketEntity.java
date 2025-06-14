package com.jose.ticket.domain.ticketinfo.entity;

import com.jose.ticket.domain.category.entity.Category;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(name = "ticket_title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "ticket_event_start_datetime")
    private LocalDateTime eventStartDatetime;

    @Column(name = "ticket_event_end_datetime")
    private LocalDateTime eventEndDatetime;

    @Column(name = "ticket_price")
    private String price;

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

    //  새로 추가된 필드들
    @Column(name = "ticket_age_limit", length = 50)
    private String ageLimit;

    @Column(name = "ticket_event_time", columnDefinition = "TEXT")
    private String eventTime;

    @Column(name = "ticket_description_url", length = 500)
    private String descriptionUrl;

    @Column(name = "view_count")
    private Integer viewCount;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, Category category,
                       LocalDateTime eventStartDatetime, LocalDateTime eventEndDatetime,
                       String price,
                       String venue, String bookingLink, String bookingProvider,
                       LocalDateTime bookingDatetime, String imageUrl,
                       String ageLimit, String eventTime, String descriptionUrl) {

        this.title = title;
        this.category = category;
        this.eventStartDatetime = eventStartDatetime;
        this.eventEndDatetime = eventEndDatetime;
        this.price = price;
        this.venue = venue;
        this.bookingLink = bookingLink;
        this.bookingProvider = bookingProvider;
        this.bookingDatetime = bookingDatetime;
        this.imageUrl = imageUrl;
        this.ageLimit = ageLimit;
        this.eventTime = eventTime;
        this.descriptionUrl = descriptionUrl;
    }
}
