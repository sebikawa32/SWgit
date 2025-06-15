package com.jose.ticket.domain.notification.service;

import com.jose.ticket.domain.notification.dto.NotificationResponseDto;
import com.jose.ticket.domain.notification.entity.Notification;
import com.jose.ticket.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationSseService notificationSseService;

    // 알림 생성 //db에 저장 후 SseServiceNotification으로 실시간 푸쉬 추가
    public Notification createNotification(Long userId, String type, String content,
                                           String url, String targetType, Long targetId) {
        System.out.println("✅ [알림 생성] userId = " + userId + " (" + userId.getClass().getName() + ")");

        System.out.println("1️⃣ [알림 생성 서비스] 요청 들어옴 userId = " + userId);

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
        System.out.println("2️⃣ [알림 생성 서비스] DB 저장 완료, savedId = " + saved.getNotificationId());

        // DTO로 변환해서 실시간 알림도 전송
        NotificationResponseDto responseDto = NotificationResponseDto.builder()
                .notificationId(saved.getNotificationId())
                .type(saved.getType())
                .content(saved.getContent())
                .url(saved.getUrl())
                .isRead(saved.getIsRead())
                .createdAt(saved.getCreatedAt().toString()) // 포맷팅은 원하는대로
                .build();
        System.out.println("3️⃣ [알림 생성 서비스] DTO 변환 완료, DTO = " + responseDto);

        notificationSseService.sendNotification(userId, responseDto);
        System.out.println("4. 실시간 알림 PUSH 완료! → userId=" + userId);

        return saved;
    }

    // 내 알림 리스트 조회 (최신순)
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 알림 읽음 처리
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
}
