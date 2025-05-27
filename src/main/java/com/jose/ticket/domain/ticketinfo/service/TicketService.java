package com.jose.ticket.domain.ticketinfo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.jose.ticket.domain.category.entity.Category;
import com.jose.ticket.domain.category.repository.CategoryRepository;
import com.jose.ticket.domain.ticketinfo.dto.TicketDetailResponseDto;
import com.jose.ticket.global.exception.TicketNotFoundException;
import com.jose.ticket.domain.ticketinfo.dto.TicketRequestDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository; //  카테고리 리포지토리 주입

    //  전체 티켓 조회
    public List<TicketResponseDto> getAllTickets() {
        List<TicketEntity> all = ticketRepository.findAll();
        return all.stream()
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }

    //  티켓 등록
    public TicketResponseDto addTicket(TicketRequestDto requestDto) {
        // 카테고리 ID로 Category 조회
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + requestDto.getCategoryId()));

        TicketEntity ticket = TicketEntity.builder()
                .title(requestDto.getTitle())
                .category(category)
                .eventDatetime(requestDto.getEventDatetime())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .venue(requestDto.getVenue())
                .bookingLink(requestDto.getBookingLink())
                .bookingProvider(requestDto.getBookingProvider())
                .bookingDatetime(requestDto.getBookingDatetime())
                .imageUrl(requestDto.getImageUrl())
                .build();

        TicketEntity saved = ticketRepository.save(ticket);
        return new TicketResponseDto(saved);
    }

    //  티켓 삭제
    public void deleteTicket(Long id) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        ticketRepository.delete(ticket);
    }

    // 티켓 수정
    public TicketResponseDto updateTicket(Long id, TicketRequestDto requestDto) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        // 수정 시에도 categoryId로 Category 찾아서 적용
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + requestDto.getCategoryId()));

        ticket.update(
                requestDto.getTitle(),
                category,
                requestDto.getEventDatetime(),
                requestDto.getPrice(),
                requestDto.getDescription(),
                requestDto.getVenue(),
                requestDto.getBookingLink(),
                requestDto.getBookingProvider(),
                requestDto.getBookingDatetime(),
                requestDto.getImageUrl()
        );

        TicketEntity updated = ticketRepository.save(ticket);
        return new TicketResponseDto(updated);
    }

    //상세보기 메서드
    public TicketDetailResponseDto getTicketDetail(Long id) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        return new TicketDetailResponseDto(
                ticket.getImageUrl(),
                ticket.getTitle(),
                ticket.getEventDatetime(),
                ticket.getPrice(),
                ticket.getDescription(),
                ticket.getVenue(),
                ticket.getBookingLink(),
                ticket.getBookingProvider(),
                ticket.getBookingDatetime()
        );
    }

    // 마감일 순 티켓 조회 (예매 마감일 기준 오름차순)
    public List<TicketResponseDto> getTicketsOrderByDeadline() {
        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<TicketEntity> tickets = ticketRepository.findByBookingDatetimeAfter(now);
        return tickets.stream().map(TicketResponseDto::new).collect(Collectors.toList());
    }

    //특정 카테고리에 속하는 티켓 목록 조회
    public List<TicketResponseDto> getTicketsByCategory(Long categoryId) {
        // 카테고리 존재 여부 확인 (필요시)
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + categoryId));

        // 해당 카테고리에 속하는 티켓 조회
        List<TicketEntity> tickets = ticketRepository.findByCategoryId(categoryId);

        // DTO 변환 후 반환
        return tickets.stream()
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }




}
