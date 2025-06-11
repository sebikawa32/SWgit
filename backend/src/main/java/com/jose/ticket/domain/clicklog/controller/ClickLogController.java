package com.jose.ticket.domain.clicklog.controller;

import com.jose.ticket.domain.clicklog.service.ClickLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class ClickLogController {

    private final ClickLogService clickLogService;

    @PostMapping("/{ticketId}/click")
    public void clickTicket(@PathVariable Long ticketId,
                            @RequestParam(required = false) Long userId) {
        clickLogService.logClick(ticketId, userId);
    }
}
