package com.jose.ticket.domain.ticketinfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

// TicketInfo 엔티티를 조작하는 JPA 리포지토리
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    // JpaRepository 덕분에 findAll(), save(), findById() 등 메서드를 자동으로 쓸 수 있음

    //현재 시각 이후의 예매 마감일을 가진 티켓을 예매 마감일 오름차순으로 조회
    @Query("SELECT t FROM TicketEntity t WHERE t.bookingDatetime >= CURRENT_TIMESTAMP ORDER BY t.bookingDatetime ASC")
    List<TicketEntity> findByBookingDatetimeAfter(LocalDateTime dateTime);

    //특정 카테고리에 속하는 티켓들을 조회
    List<TicketEntity> findByCategoryId(Long categoryId);

    // 티켓 제목에 키워드가 포함된 티켓 리스트 검색 (대소문자 무시)
    List<TicketEntity> findByTitleContainingIgnoreCase(String keyword);

    //  제목 + 카테고리 ID 조건 모두 만족하는 티켓들 검색
    List<TicketEntity> findByTitleContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId);
}
