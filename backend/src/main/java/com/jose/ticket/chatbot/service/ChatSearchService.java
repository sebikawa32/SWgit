package com.jose.ticket.chatbot.service;

import com.jose.ticket.chatbot.dto.ChatbotFilter;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatSearchService {

    private final TicketRepository ticketRepository;

    public List<TicketResponseDto> search(ChatbotFilter filter) {
        System.out.println("🧠 [ChatSearchService] 필터 입력 값: " + filter);

        if (filter.getCategoryId() == null &&
                filter.getVenue() == null &&
                filter.getPriceMax() == null &&
                filter.getStartDate() == null &&
                filter.getEndDate() == null &&
                filter.getTitle() == null &&
                (filter.getBookingProvider() == null || filter.getBookingProvider().isBlank())) {
            System.out.println("❌ 모든 필터가 null입니다. 전체 티켓 노출을 방지합니다.");
            return List.of();
        }

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (filter.getStartDate() != null) {
            startDateTime = filter.getStartDate().atStartOfDay();
        }

        if (filter.getEndDate() != null) {
            endDateTime = filter.getEndDate().atTime(23, 59, 59);
        }

        // ✅ 키워드 분해: "여름에 어울리는 연극" → ["여름", "어울리는", "연극"]
        List<String> titleKeywords = new ArrayList<>();
        if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
            for (String word : filter.getTitle().split("\\s+")) {
                if (word.length() >= 2) {
                    titleKeywords.add(word);
                }
            }
        }

        String keyword1 = titleKeywords.size() > 0 ? titleKeywords.get(0) : null;
        String keyword2 = titleKeywords.size() > 1 ? titleKeywords.get(1) : null;
        String keyword3 = titleKeywords.size() > 2 ? titleKeywords.get(2) : null;

        // ✅ 다중 키워드용 동적 쿼리 호출
        List<TicketEntity> tickets = ticketRepository.findByDynamicFilterWithTitleKeywords(
                filter.getCategoryId(),
                filter.getVenue(),
                filter.getPriceMax(),
                startDateTime,
                endDateTime,
                keyword1,
                keyword2,
                keyword3,
                filter.getAgeLimit(),
                filter.getBookingProvider()
        );

        System.out.println("📦 [ChatSearchService] 검색 결과 수: " + tickets.size());

        return tickets.stream()
                .map(TicketResponseDto::from)
                .toList();
    }
}
