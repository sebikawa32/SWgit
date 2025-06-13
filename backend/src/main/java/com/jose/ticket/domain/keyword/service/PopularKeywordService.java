package com.jose.ticket.domain.keyword.service;

import com.jose.ticket.domain.keyword.dto.PopularKeywordResponse;
import com.jose.ticket.domain.keyword.entity.PopularKeyword;
import com.jose.ticket.domain.keyword.repository.PopularKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularKeywordService {

    private final PopularKeywordRepository popularKeywordRepository;

    public List<PopularKeywordResponse> getPopularKeywords(int limit) {
        List<PopularKeyword> keywords = popularKeywordRepository.findTop10ByOrderByCountDesc();
        return keywords.stream().map(k -> {
            PopularKeywordResponse res = new PopularKeywordResponse();
            res.setKeyword(k.getNormalizedKeyword());
            res.setCount(k.getCount());
            return res;
        }).collect(Collectors.toList());
    }
}
