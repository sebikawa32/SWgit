package com.jose.ticket.domain.bookmark.repository;

import com.jose.ticket.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** 북마크 관련 DB 작업 인터페이스 **/
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // ✅ 특정 티켓의 북마크 수 조회
    int countByTicketId(Long ticketId);

    // 사용자와 티켓으로 북마크 삭제
    void deleteByUserIdAndTicketId(Long userId, Long ticketId);

    // 사용자 ID로 북마크 + 티켓 + 카테고리 함께 조회
    @Query("SELECT b FROM Bookmark b " +
            "JOIN FETCH b.ticket t " +
            "LEFT JOIN FETCH t.category " +
            "WHERE b.userId = :userId")
    List<Bookmark> findAllWithTicketAndCategoryByUserId(@Param("userId") Long userId);

    // 사용자 ID로 북마크 목록 조회
    List<Bookmark> findAllByUserId(Long userId);

    // 사용자와 티켓으로 북마크 단건 조회
    Optional<Bookmark> findByUserIdAndTicketId(Long userId, Long ticketId);

    // 특정 유저가 티켓을 북마크했는지 여부
    boolean existsByUserIdAndTicketId(Long userId, Long ticketId);

    // ✅ 회원 탈퇴 시 사용자 ID 기준으로 전체 북마크 삭제
    @Transactional
    void deleteByUserId(Long userId);
}
