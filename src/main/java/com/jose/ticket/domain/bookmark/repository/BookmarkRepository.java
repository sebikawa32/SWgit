package com.jose.ticket.domain.bookmark.repository;

import com.jose.ticket.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 특정 사용자와 티켓에 해당하는 즐겨찾기 조회
    Optional<Bookmark> findByUserIdAndTicketId(Long userId, Long ticketId);

    // 특정 사용자와 티켓에 해당하는 즐겨찾기 삭제
    void deleteByUserIdAndTicketId(Long userId, Long ticketId);

    // 유저 ID로 즐겨찾기 전체 리스트 조회 (ticket 등 필요한 필드 함께 조회)
    List<Bookmark> findAllByUserId(Long userId);
}
