package com.jose.ticket.chatbot.service;

import com.jose.ticket.chatbot.dto.ChatbotFilter;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatSearchService {

    private final TicketRepository ticketRepository;

    public List<TicketResponseDto> search(ChatbotFilter filter) {
        // ✅ 필터 파라미터 로깅
        System.out.println("🧠 [ChatSearchService] 필터 입력 값: " + filter);

        // ✅ 자주 사용하는 필터가 전부 비어있으면 검색 중단
        if (filter.getCategoryId() == null &&
                filter.getVenue() == null &&
                filter.getPriceMax() == null &&
                filter.getStartDate() == null &&
                filter.getEndDate() == null) {
            System.out.println("❌ 주요 필터가 모두 비어 있어 전체 티켓 노출을 방지합니다.");
            return List.of(); // 빈 리스트 반환
        }

        // ✅ LocalDate → LocalDateTime 변환
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (filter.getStartDate() != null) {
            startDateTime = filter.getStartDate().atStartOfDay(); // 00:00:00
        }

        if (filter.getEndDate() != null) {
            endDateTime = filter.getEndDate().atTime(23, 59, 59); // 23:59:59
        }

        // ✅ 조건 기반 검색
        List<TicketEntity> tickets = ticketRepository.findByDynamicFilter(
                filter.getCategoryId(),
                filter.getVenue(),
                filter.getPriceMax(),
                startDateTime,
                endDateTime,
                filter.getTitle(),
                filter.getAgeLimit(),
                filter.getBookingProvider()
        );

        // ✅ 결과 개수 로깅
        System.out.println("📦 [ChatSearchService] 검색 결과 수: " + tickets.size());

        return tickets.stream()
                .map(TicketResponseDto::from)
                .toList();
    }
}
