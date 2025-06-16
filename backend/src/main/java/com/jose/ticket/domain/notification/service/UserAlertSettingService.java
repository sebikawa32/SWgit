package com.jose.ticket.domain.notification.service;

import com.jose.ticket.domain.notification.entity.UserAlertSetting;
import com.jose.ticket.domain.notification.repository.UserAlertSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAlertSettingService {

    private final UserAlertSettingRepository repository;

    // 알림 설정 저장
    public void saveAlertSetting(Long userId, Long ticketId, int alertMinutes, boolean emailEnabled) {
        log.info("📝 알림 설정 저장 시도 → userId={}, ticketId={}, alertMinutes={}, emailEnabled={}",
                userId, ticketId, alertMinutes, emailEnabled);

        boolean exists = repository.existsByUserIdAndTicketIdAndAlertMinutes(userId, ticketId, alertMinutes);
        if (exists) {
            log.warn("⚠️ 이미 동일한 알림 설정이 존재 → 저장 중단");
            throw new IllegalArgumentException("이미 해당 알림 설정이 존재합니다.");
        }

        UserAlertSetting setting = UserAlertSetting.builder()
                .userId(userId)
                .ticketId(ticketId)
                .alertMinutes(alertMinutes)
                .emailEnabled(emailEnabled)
                .build();

        repository.save(setting);
        log.info("✅ 알림 설정 저장 완료 → alertId={}, userId={}, ticketId={}",
                setting.getAlertId(), userId, ticketId);
    }
}
