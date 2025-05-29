package com.jose.ticket.domain.search.controller;

import org.springframework.web.bind.annotation.*;
import com.jose.ticket.domain.search.dto.SearchRequestDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.search.service.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // GET /api/search?query=...&categoryId=...
    @GetMapping
    public List<TicketResponseDto> searchTickets(@ModelAttribute SearchRequestDto request) {
        return searchService.searchTickets(request);
    }
}
