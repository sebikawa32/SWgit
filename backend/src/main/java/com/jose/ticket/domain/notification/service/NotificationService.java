package com.jose.ticket.domain.notification.service;

import com.jose.ticket.domain.notification.dto.NotificationResponseDto;
import com.jose.ticket.domain.notification.entity.Notification;
import com.jose.ticket.domain.notification.repository.NotificationRepository;
import com.jose.ticket.domain.notification.repository.UserAlertSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationSseService notificationSseService;
    private final UserAlertSettingRepository userAlertSettingRepository;

    // 알림 생성
    public Notification createNotification(Long userId, String type, String content,
                                           String url, String targetType, Long targetId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .content(content)
                .url(url)
                .targetType(targetType)
                .targetId(targetId)
                .isRead(false)
                .build();
        Notification saved = notificationRepository.save(notification);

        NotificationResponseDto responseDto = NotificationResponseDto.builder()
                .notificationId(saved.getNotificationId())
                .type(saved.getType())
                .content(saved.getContent())
                .url(saved.getUrl())
                .isRead(saved.getIsRead())
                .createdAt(saved.getCreatedAt().toString())
                .build();

        notificationSseService.sendNotification(userId, responseDto);
        return saved;
    }

    // 내 알림 리스트 조회 (최신순)
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 개별 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림이 존재하지 않습니다"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    // 안읽은 알림 개수
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // 모두 읽음 처리
    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllReadByUserId(userId);
    }
}
