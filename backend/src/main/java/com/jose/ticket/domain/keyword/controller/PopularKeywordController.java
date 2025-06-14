package com.jose.ticket.domain.keyword.controller;

import com.jose.ticket.domain.keyword.dto.PopularKeywordResponse;
import com.jose.ticket.domain.keyword.service.PopularKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class PopularKeywordController {

    private final PopularKeywordService popularKeywordService;

    @GetMapping("/popular")
    public List<PopularKeywordResponse> getPopularKeywords(
            @RequestParam(defaultValue = "10") int limit) {
        return popularKeywordService.getPopularKeywords(limit);
    }
}
