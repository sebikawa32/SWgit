package com.jose.ticket.domain.clicklog.controller;

import com.jose.ticket.domain.clicklog.service.ClickLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class ClickLogController {

    private final ClickLogService clickLogService;


    @PostMapping("/{ticketId}/click")
    public void clickTicket(@PathVariable Long ticketId,
                            @RequestParam(required = false) Long userId) {
        System.out.println("click 로그 컨트롤러 호출됨!");
        clickLogService.logClick(ticketId, userId);
    }
}
