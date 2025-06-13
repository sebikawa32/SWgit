package com.jose.ticket.domain.keyword.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "popular_keyword")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PopularKeyword {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String normalizedKeyword;

    private Integer count;

    private LocalDateTime updatedAt;
}
