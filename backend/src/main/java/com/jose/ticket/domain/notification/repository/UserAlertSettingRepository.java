package com.jose.ticket.domain.notification.repository;

import com.jose.ticket.domain.notification.entity.UserAlertSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAlertSettingRepository extends JpaRepository<UserAlertSetting, Long> {
    List<UserAlertSetting> findByUserId(Long userId);
    List<UserAlertSetting> findByTicketId(Long ticketId);
    List<UserAlertSetting> findByAlertMinutesLessThan(Integer alertMinutes);// ★이름 정확히
    Optional<UserAlertSetting> findByUserIdAndTicketId(Long userId, Long ticketId);
    boolean existsByUserIdAndTicketIdAndAlertMinutes(Long userId, Long ticketId, Integer alertMinutes);

}

