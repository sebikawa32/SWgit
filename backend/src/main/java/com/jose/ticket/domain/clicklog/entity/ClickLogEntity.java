package com.jose.ticket.domain.clicklog.entity;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_click_log")
public class ClickLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "click_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @Column(name = "user_id")
    private Long userId; // 비회원이면 null

    @Column(name = "clicked_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime clickedAt;

    @PrePersist
    protected void onCreate() {
        this.clickedAt = LocalDateTime.now();
    }

    // Getter, Setter (필요에 따라 추가)
    public Long getId() { return id; }
    public TicketEntity getTicket() { return ticket; }
    public Long getUserId() { return userId; }
    public LocalDateTime getClickedAt() { return clickedAt; }

    public void setTicket(TicketEntity ticket) { this.ticket = ticket; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }
}
