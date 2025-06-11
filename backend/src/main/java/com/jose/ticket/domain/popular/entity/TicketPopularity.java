package com.jose.ticket.domain.popular.entity;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_popularity")
public class TicketPopularity {
    @Id
    @Column(name = "ticket_id")
    private Long ticketId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private TicketEntity ticket;

    @Column(name = "popularity_score")
    private Float popularityScore;

    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    // --- Getter/Setter ---
    public Long getTicketId() { return ticketId; }
    public TicketEntity getTicket() { return ticket; }
    public Float getPopularityScore() { return popularityScore; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public void setTicket(TicketEntity ticket) { this.ticket = ticket; }
    public void setPopularityScore(Float popularityScore) { this.popularityScore = popularityScore; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}