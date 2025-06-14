package com.jose.ticket.domain.popular.controller;

import com.jose.ticket.domain.popular.service.PopularityService;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class PopularityController {

    private final PopularityService popularityService;

    // 카테고리 전체 인기순
    @GetMapping("/popular")
    public List<TicketResponseDto> getPopularTickets(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "20") int size) {
        return popularityService.getPopularTickets(categoryId, size);
    }

}