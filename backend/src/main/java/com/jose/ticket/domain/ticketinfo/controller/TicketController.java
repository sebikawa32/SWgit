package com.jose.ticket.domain.ticketinfo.controller;

import java.util.List;

import com.jose.ticket.domain.ticketinfo.dto.TicketDetailResponseDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketRequestDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketSummaryDto;
import com.jose.ticket.domain.ticketinfo.service.TicketService;
import com.jose.ticket.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // ✅ 전체 티켓 목록 조회
    @GetMapping
    public List<TicketResponseDto> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // ✅ 티켓 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponseDto> getTicketById(@PathVariable Long id) {
        TicketDetailResponseDto response = ticketService.getTicketDetail(id);
        return ResponseEntity.ok(response);
    }

    // ✅ 마감일 순 정렬된 티켓 목록 조회
    @GetMapping("/deadline")
    public ResponseEntity<List<TicketResponseDto>> getTicketsOrderByDeadline() {
        List<TicketResponseDto> response = ticketService.getTicketsOrderByDeadline();
        return ResponseEntity.ok(response);
    }

    // ✅ 카테고리별 전체 티켓 목록 조회 (페이징 없이)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByCategory(@PathVariable Long categoryId) {
        List<TicketResponseDto> result = ticketService.getTicketsByCategory(categoryId.intValue());
        return ResponseEntity.ok(result);
    }

    // ✅ 카테고리별 티켓 목록 조회 (페이지네이션)
    @GetMapping("/category/{categoryId}/page")
    public Page<TicketResponseDto> getTicketsByCategoryWithPaging(
            @PathVariable int categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventStartDatetime").descending());
        return ticketService.getTicketsByCategory(categoryId, pageable);
    }

    // ✅ 요약 DTO 조회
    @GetMapping("/summaries")
    public List<TicketSummaryDto> getTicketSummaries() {
        return ticketService.getAllTickets().stream()
                .map(t -> new TicketSummaryDto(t.getId(), t.getTitle()))
                .toList();
    }

    // ✅ 공연 시작일 기준 가까운 순 정렬 (오늘 이후 공연만)
    @GetMapping("/sorted")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByCategorySorted(@RequestParam int categoryId) {
        return ResponseEntity.ok(ticketService.getUpcomingTicketsByCategory(categoryId));
    }

    @GetMapping("/sorted/page")
    public Page<TicketResponseDto> getSortedTicketsPaged(
            @RequestParam int categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventStartDatetime").ascending());
        return ticketService.getUpcomingTicketsByCategoryPaged(categoryId, pageable);
    }

    // ✅ 티켓 생성 (관리자만)
    @PostMapping
    public ResponseEntity<TicketResponseDto> addTicket(
            @RequestBody TicketRequestDto requestDto,
            @AuthenticationPrincipal User loginUser
    ) {
        return ResponseEntity.ok(ticketService.addTicket(requestDto, loginUser));
    }

    // ✅ 티켓 수정 (관리자만)
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDto> updateTicket(
            @PathVariable Long id,
            @RequestBody TicketRequestDto requestDto,
            @AuthenticationPrincipal User loginUser
    ) {
        return ResponseEntity.ok(ticketService.updateTicket(id, requestDto, loginUser));
    }

    // ✅ 티켓 삭제 (관리자만)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal User loginUser
    ) {
        ticketService.deleteTicket(id, loginUser);
        return ResponseEntity.noContent().build();
    }
}
