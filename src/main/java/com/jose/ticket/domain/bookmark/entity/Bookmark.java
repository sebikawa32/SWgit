package com.jose.ticket.domain.bookmark.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


 // 즐겨찾기(Bookmark) 엔티티 클래스
 // DB의 bookmark 테이블과 매핑됨
 // 각 즐겨찾기 항목은 사용자(userId)와 티켓(ticketId)의 관계를 나타냄

@Getter
@Setter
@Entity
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //즐겨찾기 고유 ID (자동 생성)

    private Long userId; //즐겨찾기한 사용자 ID

    private Long ticketId; // 즐겨찾기된 티켓 ID

}
