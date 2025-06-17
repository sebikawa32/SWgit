package com.jose.ticket.domain.search.service;

import com.jose.ticket.domain.search.dto.SearchRequestDto;
import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final TicketRepository ticketRepository;

    public SearchService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // 불용어 리스트
    private static final Set<String> STOPWORDS = Set.of(
            "의", "이", "가", "은", "는", "을", "를", "에", "에서",
            "와", "과", "도", "으로", "로", "및", "에게", "한테", "께", "께서",
            "밖에", "마저", "까지", "부터", "이나", "나", "라도", "처럼", "공연", "콘서트",
            "추천"
    );

    // 키워드 전처리 (불용어, 특수문자 제거 & 분리)
    public static List<String> splitKeywords(String keyword) {
        // 특수문자 제거
        String cleaned = keyword.replaceAll("[^\\p{L}\\p{N}\\s]", " ");
        // 한글 조사를 불용어로 처리
        for (String stopword : STOPWORDS) {
            cleaned = cleaned.replace(stopword, " ");
        }
        // 여러 칸 공백 -> 하나로
        cleaned = cleaned.replaceAll("\\s+", " ");
        String[] keywords = cleaned.trim().split(" ");
        return Arrays.stream(keywords)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public List<TicketResponseDto> searchTickets(SearchRequestDto request) {
        String keyword = request.getQuery() != null ? request.getQuery().trim() : "";
        Integer categoryId = request.getCategoryId();
        List<TicketEntity> tickets;

        if (keyword.isEmpty()) {
            tickets = (categoryId == null || categoryId == 0)
                    ? ticketRepository.findAll()
                    : ticketRepository.findByCategoryId(categoryId);
        } else {
            List<String> keywords = splitKeywords(keyword);
            if (keywords.isEmpty()) {
                // 전처리 후 남은 키워드가 없으면 전체 반환
                tickets = (categoryId == null || categoryId == 0)
                        ? ticketRepository.findAll()
                        : ticketRepository.findByCategoryId(categoryId);
            } else {
                // 붙어쓰기/띄어쓰기 무시하고 모두 포함되는 항목만 반환
                List<TicketEntity> all = (categoryId == null || categoryId == 0)
                        ? ticketRepository.findAll()
                        : ticketRepository.findByCategoryId(categoryId);

                tickets = all.stream()
                        .filter(t -> {
                            // 띄어쓰기 제거한 title로 비교
                            String target = t.getTitle().replaceAll("\\s", "");
                            return keywords.stream()
                                    .allMatch(k -> target.contains(k.replaceAll("\\s", "")));
                        })
                        .collect(Collectors.toList());
            }
        }
        return tickets.stream().map(TicketResponseDto::new).toList();
    }
}
