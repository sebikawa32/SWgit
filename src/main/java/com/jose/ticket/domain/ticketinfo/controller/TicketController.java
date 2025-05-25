package com.jose.ticket.domain.ticketinfo.controller;

import java.util.List;

import com.jose.ticket.domain.ticketinfo.dto.TicketRequestDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.service.TicketService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 티켓 추가
    @PostMapping
    public ResponseEntity<TicketResponseDto> createTicket(@RequestBody TicketRequestDto dto) {
        TicketResponseDto saved = ticketService.addTicket(dto);         //saved는 방금 저장된 티켓의 정보를 담은 DTO
        return new ResponseEntity<>(saved, HttpStatus.CREATED);         //상태코드 201 (잘 저장된 상태를 의미)
    }

    // 티켓 삭제
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build(); // 204 No Content 반환 - 삭제 처리 완료
    }

    //티켓 수정
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDto> updateTicket(@PathVariable Long id, @RequestBody TicketRequestDto dto) {
        TicketResponseDto updated = ticketService.updateTicket(id, dto);
        System.out.println("수정할 티켓 ID: " + id);
        return ResponseEntity.ok(updated);
    }

}

