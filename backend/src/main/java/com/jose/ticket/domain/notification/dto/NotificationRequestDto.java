package com.jose.ticket.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 알림 생성 요청 DTO
 * (알림 만들 때 클라이언트가 보내는 값들)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {
    private Long userId;         // 알림 받을 유저 ID
    private String type;         // 알림 유형(DAY, COMMENT, NOTICE 등)
    private String content;      // 알림 메시지
    private String url;          // 클릭 시 이동 경로
    private String targetType;   // 알림 대상 타입(POST, TICKET, NOTICE 등)
    private Long targetId;       // 알림 대상 PK
}
