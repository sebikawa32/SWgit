package com.jose.ticket.domain.search.controller;

import com.jose.ticket.domain.board.dto.BoardResponse;
import com.jose.ticket.domain.search.dto.SearchRequestDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.search.service.SearchService;
import com.jose.ticket.domain.board.service.BoardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final BoardService boardService;

    public SearchController(SearchService searchService, BoardService boardService) {
        this.searchService = searchService;
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam("query") String query,
            @RequestParam(value = "categoryId", required = false) Integer categoryId
    ) {
        System.out.println("📦 query = " + query + ", categoryId = " + categoryId);

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 검색어는 필수입니다.");
        }

        try {
            SearchRequestDto request = new SearchRequestDto(query, categoryId);

            // 🎫 티켓 검색
            List<TicketResponseDto> tickets = searchService.searchTickets(request);
            System.out.println("✅ 티켓 검색 성공");

            // 📝 게시글 검색 (원래대로)
            List<BoardResponse> boards = boardService.searchBoards(query);
            System.out.println("✅ 게시글 검색 성공");

            // 📦 통합 결과 반환
            Map<String, Object> result = new HashMap<>();
            result.put("tickets", tickets);
            result.put("boards", boards);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.out.println("❌ 검색 중 예외 발생");
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ 서버 오류: " + e.getMessage());
        }
    }
}
