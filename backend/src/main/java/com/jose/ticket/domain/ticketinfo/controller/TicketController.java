package com.jose.ticket.domain.ticketinfo.controller;

import java.util.List;

import com.jose.ticket.domain.ticketinfo.dto.TicketDetailResponseDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.service.TicketService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    // 전체 티켓 목록 조회
    @GetMapping
    public List<TicketResponseDto> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // 티켓 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponseDto> getTicketById(@PathVariable Long id) {
        TicketDetailResponseDto response = ticketService.getTicketDetail(id);
        return ResponseEntity.ok(response);
    }


    //  마감일 순 정렬된 티켓 목록 조회
    @GetMapping("/deadline")
    public ResponseEntity<List<TicketResponseDto>> getTicketsOrderByDeadline() {
        List<TicketResponseDto> response = ticketService.getTicketsOrderByDeadline();
        return ResponseEntity.ok(response);
    }

    // 카테고리별 티켓 목록 조회 API
    @GetMapping("/category/{categoryId}")
    public List<TicketResponseDto> getTicketsByCategory(@PathVariable Long categoryId) {
        return ticketService.getTicketsByCategory(categoryId);
    }
}
