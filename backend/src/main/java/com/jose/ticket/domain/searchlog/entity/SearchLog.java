package com.jose.ticket.domain.searchlog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_log")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SearchLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // nullable

    @Column(length = 100, nullable = false)
    private String originalKeyword;

    @Column(length = 100, nullable = false)
    private String normalizedKeyword;

    private LocalDateTime searchedAt;

    @PrePersist
    protected void onCreate() {
        this.searchedAt = LocalDateTime.now();
    }
}
