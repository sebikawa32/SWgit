package com.jose.ticket.domain.notification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long notificationId; // 알림 PK
    private String type;         // 알림 유형(DDAY, COMMENT, NOTICE 등)
    private String content;      // 알림 메시지
    private String url;          // 클릭시 이동 경로
    private Boolean isRead;      // 읽음 여부
    private String createdAt;    // 알림 발생 시각(YYYY-MM-DD HH:mm) 등으로 포맷해서 내려주면 UI에 바로 쓸 수 있음
    private String timeAgo;      // "5분 전", "2시간 전" 처럼 표시 (서비스단에서 계산해서 넣어주면 편함)
}
