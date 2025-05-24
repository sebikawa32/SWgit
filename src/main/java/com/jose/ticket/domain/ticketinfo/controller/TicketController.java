package com.jose.ticket.domain.tickets.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jose.ticket.domain.tickets.dto.TicketResponseDto;
import com.jose.ticket.domain.tickets.service.TicketService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

//이 패키지는 티켓 목록을 불러옵니다만..

@RestController
@RequestMapping("/api/tickets") // 이 컨트롤러는 "/api/tickets"로 시작하는 URL 처리
@RequiredArgsConstructor // 생성자 자동 생성 (final 필드 자동 주입)
public class TicketController {
    private final TicketService ticketService;

    // 전체 티켓 목록을 조회하는 API
    @GetMapping
    public List<TicketResponseDto> getAllTickets() {
        return ticketService.getAllTickets();
    }
}
