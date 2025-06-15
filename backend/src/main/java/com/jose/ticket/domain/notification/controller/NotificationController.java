package com.jose.ticket.domain.notification.controller;

import com.jose.ticket.domain.notification.dto.NotificationRequestDto;
import com.jose.ticket.domain.notification.entity.Notification;
import com.jose.ticket.domain.notification.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 리스트 조회
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
        return notificationService.getNotifications(userId);
    }

    // 알림 읽음 처리
    @PostMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
    }

    // 안읽은 알림 개수
    @GetMapping("/{userId}/unread-count")
    public Long getUnreadCount(@PathVariable Long userId) {
        return notificationService.getUnreadCount(userId);
    }

    // (테스트용) 알림 생성 API - 실제론 게시판/공연 등에서 서비스 호출하게 만들면 됨
    @PostMapping("/test-create")
    public Notification createNotification(@RequestBody NotificationRequestDto req) {
        return notificationService.createNotification(
                req.getUserId(), req.getType(), req.getContent(),
                req.getUrl(), req.getTargetType(), req.getTargetId()
        );
    }

}