package com.jose.ticket.domain.bookmark.repository;

import com.jose.ticket.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    void deleteByUserIdAndTicketId(Long userId, Long ticketId);
}
