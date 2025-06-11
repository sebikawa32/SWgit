package com.jose.ticket.domain.popular.service;

import com.jose.ticket.domain.popular.entity.TicketPopularity;
import com.jose.ticket.domain.popular.repository.PopularityRepository;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularityService {

    private final PopularityRepository popularityRepository;

    public List<TicketResponseDto> getPopularTickets(int size) {
        return popularityRepository.findAllByOrderByPopularityScoreDesc()
                .stream()
                .limit(size)
                .map(popularity -> new TicketResponseDto(popularity.getTicket()))
                .collect(Collectors.toList());
    }
}

