package com.jose.ticket.domain.board.repository;

import com.jose.ticket.domain.board.entity.Comment;
import com.jose.ticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId);

    // ✅ 수정: Comment 엔티티에 있는 필드명이 'writer'일 경우
    List<Comment> findByWriter(User user);
}