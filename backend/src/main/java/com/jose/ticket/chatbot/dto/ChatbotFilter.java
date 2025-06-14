package com.jose.ticket.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotFilter {

    private String title;              // 공연명 키워드
    private Integer categoryId;        // 공연 카테고리 ID
    private String venue;              // 공연 장소/지역
    private Integer priceMax;          // 최대 가격

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;       // 공연 시작일

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;         // 공연 종료일

    private String ageLimit;           // 관람 가능 연령
    private String bookingProvider;    // 예매처

    // toString()은 @Data에 포함되어 있음
}
