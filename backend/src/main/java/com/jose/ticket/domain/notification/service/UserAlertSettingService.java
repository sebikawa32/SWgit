package com.jose.ticket.domain.notification.service;

import com.jose.ticket.domain.notification.entity.UserAlertSetting;
import com.jose.ticket.domain.notification.repository.UserAlertSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAlertSettingService {

    private final UserAlertSettingRepository repository;

    // 1) 새 알림 저장
    public void saveAlertSetting(Long userId, Long ticketId, int alertMinutes, boolean emailEnabled) {
        log.info("알림 저장 시도 → userId={}, ticketId={}, minutes={}, emailEnabled={}",
                userId, ticketId, alertMinutes, emailEnabled
        );
        boolean exists = repository.existsByUserIdAndTicketIdAndAlertMinutes(
                userId, ticketId, alertMinutes
        );
        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 알림 설정입니다.");
        }
        UserAlertSetting setting = UserAlertSetting.builder()
                .userId(userId)
                .ticketId(ticketId)
                .alertMinutes(alertMinutes)
                .emailEnabled(emailEnabled)
                .build();
        repository.save(setting);
        log.info("알림 저장 완료 → alertId={}", setting.getAlertId());
    }

    // 2) 유저별 전체 알림 목록 조회
    public List<UserAlertSetting> getAllByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    // 3) 특정 티켓 단건 조회
    public Optional<UserAlertSetting> getAlertSetting(Long userId, Long ticketId) {
        return repository.findByUserIdAndTicketId(userId, ticketId);
    }

    // 4) 알림 수정
    public void updateAlertSetting(Long userId, Long alertId, Long ticketId, int alertMinutes, boolean emailEnabled) {
        UserAlertSetting setting = repository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정을 찾을 수 없습니다."));
        if (!setting.getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        setting.setTicketId(ticketId);
        setting.setAlertMinutes(alertMinutes);
        setting.setEmailEnabled(emailEnabled);
        repository.save(setting);
        log.info("알림 수정 완료 → alertId={}", alertId);
    }

    // 5) 알림 삭제
    public void deleteAlertSetting(Long userId, Long alertId) {
        UserAlertSetting setting = repository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정을 찾을 수 없습니다."));
        if (!setting.getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        repository.delete(setting);
        log.info("알림 삭제 완료 → alertId={}", alertId);
    }
}
