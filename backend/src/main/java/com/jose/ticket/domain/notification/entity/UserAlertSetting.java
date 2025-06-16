package com.jose.ticket.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
        name = "user_alert_setting",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ticket_id", "alert_minutes"}) // DB 컬럼명 기준!
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAlertSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "alert_minutes", nullable = false) // ex) 1440=1일전, 120=2시간전
    private Integer alertMinutes;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = false;

}
