package com.jose.ticket.domain.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationSseService {
    // 유저별 연결 관리
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        System.out.println("[subscribe] userId = " + userId + " (" + userId.getClass().getName() + ")");
        SseEmitter emitter = new SseEmitter(1000 * 60 * 60L); // 1시간짜리 커넥션
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        // 연결 성공 응답(옵션)
        try { emitter.send(SseEmitter.event().name("connect").data("connected")); } catch (IOException ignored) {}

        return emitter;
    }

    // 알림 전송 (유저에게만)
    public void sendNotification(Long userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(data));
                System.out.println("SSE 이벤트 전송 성공!");
            } catch (Exception e) {
                System.out.println("SSE PUSH 실패: " + e.getMessage());
                emitters.remove(userId); // 실패 시 연결 해제
            }
        } else{
            System.out.println("SSE emitter가 없음! (userId=" + userId + ")");
        }
    }
}
