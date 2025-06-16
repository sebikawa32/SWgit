package com.jose.ticket.domain.notification.controller;

import com.jose.ticket.domain.notification.entity.UserAlertSetting;
import com.jose.ticket.domain.notification.repository.UserAlertSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
@Slf4j
public class UserAlertSettingController {

    private final UserAlertSettingRepository alertRepo;

    @PostMapping
    public ResponseEntity<String> createAlert(@RequestBody UserAlertSetting request) {
        log.info("🔔 알림 설정 요청 수신 → userId={}, ticketId={}, alertMinutes={}, emailEnabled={}",
                request.getUserId(), request.getTicketId(), request.getAlertMinutes(), request.isEmailEnabled());

        // 이미 동일 설정이 있는 경우 예외 처리하거나 무시
        boolean exists = alertRepo.existsByUserIdAndTicketIdAndAlertMinutes(
                request.getUserId(), request.getTicketId(), request.getAlertMinutes());

        if (exists) {
            log.warn("⚠️ 이미 동일한 알림 설정이 존재합니다. 저장 생략.");
            return ResponseEntity.badRequest().body("이미 설정된 알림입니다.");
        }

        alertRepo.save(request);
        log.info("✅ 알림 설정 저장 완료! → userId={}, ticketId={}", request.getUserId(), request.getTicketId());
        return ResponseEntity.ok("알림 설정 완료!");
    }
}
