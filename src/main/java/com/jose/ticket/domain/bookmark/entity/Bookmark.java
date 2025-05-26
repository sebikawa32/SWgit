package com.jose.ticket.domain.bookmark.entity;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


// 즐겨찾기(Bookmark) 엔티티 클래스
// DB의 bookmark 테이블과 매핑됨
// 각 즐겨찾기 항목은 사용자(userId)와 티켓(ticketId)의 관계를 나타냄

@Entity
@Getter
@Setter
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;
}