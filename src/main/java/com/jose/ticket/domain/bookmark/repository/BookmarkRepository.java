package com.jose.ticket.domain.bookmark.repository;

import com.jose.ticket.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 특정 사용자와 티켓에 해당하는 즐겨찾기 조회
    Optional<Bookmark> findByUserIdAndTicketId(Long userId, Long ticketId);

    // 특정 사용자와 티켓에 해당하는 즐겨찾기 삭제
    void deleteByUserIdAndTicketId(Long userId, Long ticketId);
}
