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
        String keyword = request.getQuery() != null ? request.getQuery().trim() : "";
        Integer categoryId = request.getCategoryId();

        System.out.println("🧪 keyword: " + keyword + ", categoryId: " + categoryId + " (" + (categoryId != null ? categoryId.getClass().getSimpleName() : "null") + ")");

        List<TicketEntity> tickets;

        // ✅ keyword가 빈 문자열이면 전체 리스트로 대체
        if (keyword.isEmpty()) {
            System.out.println("🔍 키워드 없음 → 전체 리스트 반환");
            tickets = (categoryId == null || categoryId == 0)
                    ? ticketRepository.findAll()
                    : ticketRepository.findByCategoryId(categoryId);
        }
        // ✅ 키워드 존재
        else {
            if (categoryId == null || categoryId == 0) {
                System.out.println("🔍 전체 검색 (카테고리 없음)");
                tickets = ticketRepository.searchByTitleOnly(keyword);
            } else {
                System.out.println("🔍 카테고리 포함 검색: categoryId = " + categoryId);
                tickets = ticketRepository.findByTitleContainingIgnoreCaseAndCategoryId(keyword, categoryId);
            }
        }

        return tickets.stream()
                .map(TicketResponseDto::new)
                .collect(Collectors.toList());
    }
}
