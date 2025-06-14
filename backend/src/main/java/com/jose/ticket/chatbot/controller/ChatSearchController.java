package com.jose.ticket.chatbot.controller;

import com.jose.ticket.chatbot.dto.ChatRequest;
import com.jose.ticket.chatbot.dto.ChatbotFilter;
import com.jose.ticket.chatbot.service.ChatSearchService;
import com.jose.ticket.chatbot.util.ChatbotFilterExtractor;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatSearchController {

    private final ChatbotFilterExtractor filterExtractor;
    private final ChatSearchService searchService;

    @PostMapping("/search")
    public ResponseEntity<List<TicketResponseDto>> search(@RequestBody ChatRequest request) {
        System.out.println("💬 사용자 메시지: " + request.getMessage());

        ChatbotFilter filter = filterExtractor.extract(request.getMessage());
        sanitizeFilter(filter); // 문자열 "null" 방어 처리
        System.out.println("🧠 추출된 필터: " + filter);

        List<TicketResponseDto> results = searchService.search(filter);
        System.out.println("📦 검색 결과 수: " + results.size());

        return ResponseEntity.ok(results);
    }

    // 문자열 "null"을 실제 null 값으로 변경하는 방어 로직
    private void sanitizeFilter(ChatbotFilter filter) {
        if ("null".equals(filter.getTitle())) filter.setTitle(null);
        if ("null".equals(filter.getVenue())) filter.setVenue(null);
        if ("null".equals(filter.getAgeLimit())) filter.setAgeLimit(null);
        if ("null".equals(filter.getBookingProvider())) filter.setBookingProvider(null);
        // LocalDate 타입의 startDate, endDate는 JSON 파싱 실패 시 자동으로 null 처리됨
    }
}
