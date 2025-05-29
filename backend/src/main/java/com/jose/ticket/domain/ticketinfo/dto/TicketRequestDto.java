package com.jose.ticket.domain.ticketinfo.dto;

import java.time.LocalDateTime;
import com.jose.ticket.domain.category.entity.Category;
import lombok.Getter;
import lombok.Setter;

/*******요청 DTO 입니다. *******/


@Getter
@Setter
public class TicketRequestDto {
    private String title;
    private Long categoryId; // 클라이언트에서 보낼 때는 category의 ID만!
    private LocalDateTime eventDatetime;
    private Integer price;
    private String description;
    private String venue;
    private String bookingLink;
    private String bookingProvider;
    private LocalDateTime bookingDatetime;
    private String imageUrl;
}