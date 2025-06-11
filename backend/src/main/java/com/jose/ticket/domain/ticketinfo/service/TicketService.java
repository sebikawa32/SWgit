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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;

    //  전체 티켓 조회
    public List<TicketResponseDto> getAllTickets() {
        return ticketRepository.findAll().stream()
                .sorted(Comparator.comparing(TicketEntity::getCreatedAt).reversed())
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }

    // 티켓 등록
    public TicketResponseDto addTicket(TicketRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + requestDto.getCategoryId()));

        TicketEntity ticket = TicketEntity.builder()
                .title(requestDto.getTitle())
                .category(category)
                .eventStartDatetime(requestDto.getEventStartDatetime())
                .eventEndDatetime(requestDto.getEventEndDatetime())
                .price(requestDto.getPrice())
                .venue(requestDto.getVenue())
                .bookingLink(requestDto.getBookingLink())
                .bookingProvider(requestDto.getBookingProvider())
                .bookingDatetime(requestDto.getBookingDatetime())
                .imageUrl(requestDto.getImageUrl())
                .ageLimit(requestDto.getAgeLimit())
                .eventTime(requestDto.getEventTime())
                .descriptionUrl(requestDto.getDescriptionUrl())
                .build();

        return new TicketResponseDto(ticketRepository.save(ticket));
    }

    // 티켓 삭제
    public void deleteTicket(Long id) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        ticketRepository.delete(ticket);
    }

    // 티켓 수정
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
                requestDto.getVenue(),
                requestDto.getBookingLink(),
                requestDto.getBookingProvider(),
                requestDto.getBookingDatetime(),
                requestDto.getImageUrl(),
                requestDto.getAgeLimit(),
                requestDto.getEventTime(),
                requestDto.getDescriptionUrl()
        );

        return new TicketResponseDto(ticketRepository.save(ticket));
    }

    //  상세보기 + 상세보기 시 조회수 증가 로직 추가 !
    @Transactional
    public TicketDetailResponseDto getTicketDetail(Long id) {
        ticketRepository.increaseViewCount(id); // 👈 조회수 +1 추가!

        TicketEntity ticket = ticketRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        return new TicketDetailResponseDto(
                ticket,
                ticket.getCategory() != null ? ticket.getCategory().getName() : null
        );
    }



    //  마감일 정렬
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

    //  카테고리 전체 조회 (정렬만)
    public List<TicketResponseDto> getTicketsByCategory(Integer categoryId) {
        if (categoryId == null || categoryId == 0) {
            return ticketRepository.findAll().stream()
                    .sorted(Comparator.comparing(TicketEntity::getCreatedAt).reversed())
                    .map(TicketResponseDto::new)
                    .collect(Collectors.toList());
        }

        categoryRepository.findById(Long.valueOf(categoryId))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + categoryId));

        return ticketRepository.findByCategoryId(categoryId).stream()
                .sorted(Comparator.comparing(TicketEntity::getCreatedAt).reversed())
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }

    //  카테고리 페이지네이션 조회
    public Page<TicketResponseDto> getTicketsByCategory(int categoryId, Pageable pageable) {
        return ticketRepository.findByCategoryId(categoryId, pageable)
                .map(TicketResponseDto::new);
    }


    public List<TicketResponseDto> getUpcomingTicketsByCategory(int categoryId) {
        List<TicketEntity> tickets = ticketRepository.findUpcomingTicketsByCategory(categoryId);
        return tickets.stream()
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }

    public Page<TicketResponseDto> getUpcomingTicketsByCategoryPaged(int categoryId, Pageable pageable) {
        return ticketRepository.findUpcomingTicketsByCategoryPaged(categoryId, pageable)
                .map(TicketResponseDto::new);
    }

}
