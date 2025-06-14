package com.jose.ticket.domain.searchlog.service;

import com.jose.ticket.domain.searchlog.dto.SearchLogRequest;
import com.jose.ticket.domain.searchlog.entity.SearchLog;
import com.jose.ticket.domain.searchlog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;

    // 정규화 로직
    public String normalizeKeyword(String keyword) {
        if (keyword == null) return "";
        String normalized = keyword.trim().toLowerCase()
                .replaceAll("[^가-힣a-z0-9]", ""); // 특수문자 제거, 필요시 불용어 추가 처리
        // 불용어 예시
        String[] stopwords = {
                "공연", "예매", "티켓", "예약", "할인", "좌석", "시간", "일정", "위치", "장소", "가격",
                "안내", "전시", "뮤지컬", "콘서트", "연극", "페스티벌", "축제", "행사", "이벤트", "쿠폰", "후기", "추천"
        };
        for (String stop : stopwords) {
            if (normalized.endsWith(stop)) {
                normalized = normalized.substring(0, normalized.length() - stop.length());
            }
        }
        return normalized;
    }

    public void saveSearchLog(SearchLogRequest req) {
        String normalized = normalizeKeyword(req.getKeyword());
        SearchLog log = SearchLog.builder()
                .userId(req.getUserId())
                .originalKeyword(req.getKeyword())
                .normalizedKeyword(normalized)
                .build();
        searchLogRepository.save(log);
    }
}
