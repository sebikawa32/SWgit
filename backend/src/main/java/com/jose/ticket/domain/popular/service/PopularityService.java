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

    /**
     * 인기순 티켓 목록 조회 (카테고리별/전체 지원)
     * @param categoryId 카테고리 ID (null이면 전체)
     * @param size 최대 조회 개수
     * @return 인기순 티켓 DTO 리스트
     */
    public List<TicketResponseDto> getPopularTickets(Integer categoryId, int size) {
        List<TicketPopularity> list;

        if (categoryId == null) {
            // 전체 인기순
            list = popularityRepository.findAllByOrderByPopularityScoreDesc();
        } else {
            // 카테고리별 인기순
            list = popularityRepository.findByTicket_Category_IdOrderByPopularityScoreDesc(categoryId);
        }

        return list.stream()
                .limit(size)
                .map(popularity -> new TicketResponseDto(popularity.getTicket()))
                .collect(Collectors.toList());
    }
}
