package com.jose.ticket.domain.search.service;

import com.jose.ticket.domain.search.dto.SearchRequestDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final TicketRepository ticketRepository;

    public SearchService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<TicketResponseDto> searchTickets(SearchRequestDto request) {
        String keyword = request.getQuery() != null ? request.getQuery() : "";
        Long categoryId = request.getCategoryId();

        List<TicketEntity> tickets;

        if (categoryId == null) {
            tickets = ticketRepository.findByTitleContainingIgnoreCase(keyword);
        } else {
            tickets = ticketRepository.findByTitleContainingIgnoreCaseAndCategoryId(keyword, categoryId);
        }

        return tickets.stream()
                .map(TicketResponseDto::new)  // 생성자 하나로 변환
                .collect(Collectors.toList());
    }
}

