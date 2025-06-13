package com.jose.ticket.domain.searchlog.controller;

import com.jose.ticket.domain.searchlog.dto.SearchLogRequest;
import com.jose.ticket.domain.searchlog.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchLogController {

    private final SearchLogService searchLogService;

    @PostMapping("/log")
    public void logSearch(@RequestBody SearchLogRequest req) {
        searchLogService.saveSearchLog(req);
    }
}
