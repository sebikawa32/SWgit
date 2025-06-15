package com.jose.ticket.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;             // 알림 받는 유저

    @Column(nullable = false)
    private String type;             // DDAY, COMMENT, NOTICE, FAVORITE 등

    @Column(nullable = false)
    private String content;          // 알림 메시지

    private String url;              // 클릭시 이동 경로

    private String targetType;       // TICKET, POST, NOTICE 등
    private Long targetId;           // 알림 대상 PK

    private Boolean isRead = false;  // 읽음여부

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;// 생성시각
}
