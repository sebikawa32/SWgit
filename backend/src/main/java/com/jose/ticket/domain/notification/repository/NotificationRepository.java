package com.jose.ticket.domain.notification.repository;

import com.jose.ticket.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    Long countByUserIdAndIsReadFalse(Long userId);  // 안읽은 알림 개수
    // NotificationRepository.java

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.isRead = false AND n.createdAt > :after ORDER BY n.createdAt DESC")
    List<Notification> findUnreadRecent(@Param("userId") Long userId, @Param("after") LocalDateTime after);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    int markAllReadByUserId(Long userId);
}
