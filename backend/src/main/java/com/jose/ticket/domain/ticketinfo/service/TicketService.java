package com.jose.ticket.domain.ticketinfo.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.jose.ticket.domain.category.entity.Category;
import com.jose.ticket.domain.category.repository.CategoryRepository;
import com.jose.ticket.domain.ticketinfo.dto.TicketDetailResponseDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketRequestDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;
import com.jose.ticket.global.exception.TicketNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;

    // ✅ 전체 티켓 조회 (최신 등록순 정렬 적용)
    public List<TicketResponseDto> getAllTickets() {
        return ticketRepository.findAll().stream()
                .sorted(Comparator.comparing(TicketEntity::getCreatedAt).reversed())
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }

    // ✅ 티켓 등록
    public TicketResponseDto addTicket(TicketRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + requestDto.getCategoryId()));

        TicketEntity ticket = TicketEntity.builder()
                .title(requestDto.getTitle())
                .category(category)
                .eventStartDatetime(requestDto.getEventStartDatetime())
                .eventEndDatetime(requestDto.getEventEndDatetime())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .venue(requestDto.getVenue())
                .bookingLink(requestDto.getBookingLink())
                .bookingProvider(requestDto.getBookingProvider())
                .bookingDatetime(requestDto.getBookingDatetime())
                .imageUrl(requestDto.getImageUrl())
                .build();

        return new TicketResponseDto(ticketRepository.save(ticket));
    }

    // ✅ 티켓 삭제
    public void deleteTicket(Long id) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        ticketRepository.delete(ticket);
    }

    // ✅ 티켓 수정
    public TicketResponseDto updateTicket(Long id, TicketRequestDto requestDto) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + requestDto.getCategoryId()));

        ticket.update(
                requestDto.getTitle(),
                category,
                requestDto.getEventStartDatetime(),
                requestDto.getEventEndDatetime(),
                requestDto.getPrice(),
                requestDto.getDescription(),
                requestDto.getVenue(),
                requestDto.getBookingLink(),
                requestDto.getBookingProvider(),
                requestDto.getBookingDatetime(),
                requestDto.getImageUrl()
        );

        return new TicketResponseDto(ticketRepository.save(ticket));
    }

    // ✅ 상세보기 - 카테고리도 함께 조회하도록 변경
    public TicketDetailResponseDto getTicketDetail(Long id) {
        TicketEntity ticket = ticketRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        return new TicketDetailResponseDto(
                ticket.getImageUrl(),
                ticket.getTitle(),
                ticket.getEventStartDatetime(),
                ticket.getEventEndDatetime(),
                ticket.getPrice(),
                ticket.getDescription(),
                ticket.getVenue(),
                ticket.getBookingLink(),
                ticket.getBookingProvider(),
                ticket.getBookingDatetime(),
                ticket.getCategory() != null ? ticket.getCategory().getName() : null // 카테고리명 추가
        );
    }

    // ✅ 예매 시작일 기준으로 정렬 + 과거 예매일 필터링
    public List<TicketResponseDto> getTicketsOrderByDeadline() {
        LocalDateTime now = LocalDateTime.now();

        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getBookingDatetime() != null && ticket.getBookingDatetime().isAfter(now))
                .sorted(Comparator.comparing(
                        TicketEntity::getBookingDatetime,
                        Comparator.nullsLast(LocalDateTime::compareTo)
                ))
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }

    // ✅ 카테고리 기준 조회 (최신 등록순 정렬 적용)
    public List<TicketResponseDto> getTicketsByCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + categoryId));

        return ticketRepository.findByCategoryId(categoryId).stream()
                .sorted(Comparator.comparing(TicketEntity::getCreatedAt).reversed())
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }
}
