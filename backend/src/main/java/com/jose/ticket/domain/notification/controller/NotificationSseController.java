package com.jose.ticket.domain.notification.controller;

import com.jose.ticket.domain.notification.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationSseController {
    private final NotificationSseService notificationSseService;

    // SSE 구독 엔드포인트 (유저별로 접속)
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam Long userId) {
        System.out.println(" [subscribe] 진입! userId = " + userId + " (" + userId.getClass().getName() + ")");
        return notificationSseService.subscribe(userId);
        //여기까진 호출 되는거 확인
    }
}
