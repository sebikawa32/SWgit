package com.jose.ticket.domain.notification.controller;

import com.jose.ticket.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class NotificationTestController {

    private final NotificationService notificationService;

    @PostMapping("/dday")
    public ResponseEntity<String> testDdayAlert(@RequestParam Long userId,
                                                @RequestParam Long ticketId,
                                                @RequestParam int minutesBefore) {

        String type = "DDAY";
        String content = "[테스트] D-" + minutesBefore / 1440 + " 공연 예매 알림입니다!";
        String url = "/ticket/" + ticketId;

        notificationService.createNotification(
                userId,
                type,
                content,
                url,
                "TICKET",
                ticketId
        );

        return ResponseEntity.ok("알림 전송 테스트 완료!");
    }
}

