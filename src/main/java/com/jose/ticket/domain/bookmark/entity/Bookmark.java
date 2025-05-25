package com.jose.ticket.domain.bookmark.entity;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bookmark")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long bookmarkId;        // 즐겨찾기 고유 ID

    @Column(name = "user_id")
    private Long userId;            // 즐겨찾기한 사용자 ID

     //티켓과 다대일 관계 매핑 (즐겨찾기는 한 개 티켓에 연결됨)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "ticket_id")
    private TicketEntity ticket;          // 즐겨찾기된 티켓 엔티티

    @Column(name = "bookmark_created_at")
    private LocalDateTime bookmarkCreatedAt;  // 즐겨찾기 생성 시간

    // 생성 시점에 bookmarkCreatedAt 자동 세팅 (옵션)
    @PrePersist
    public void prePersist() {
        if (bookmarkCreatedAt == null) {
            bookmarkCreatedAt = LocalDateTime.now();
        }
    }
}
