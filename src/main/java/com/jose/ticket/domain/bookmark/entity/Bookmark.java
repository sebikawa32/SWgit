package com.jose.ticket.domain.bookmark.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


 // 즐겨찾기(Bookmark) 엔티티 클래스
 // DB의 bookmark 테이블과 매핑됨
 // 각 즐겨찾기 항목은 사용자(userId)와 티켓(ticketId)의 관계를 나타냄

@Getter
@Setter
@Entity
@Table(name = "bookmark")  // DB의 bookmark 테이블과 매핑됨
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")  // 즐겨찾기 고유 ID (자동 생성)
    private Long bookmarkId;

    @Column(name = "user_id")  // 즐겨찾기한 사용자 ID
    private Long userId;

    @Column(name = "ticket_id")  // 즐겨찾기된 티켓 ID
    private Long ticketId;

}
