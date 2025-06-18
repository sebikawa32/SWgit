package com.jose.ticket.domain.board.repository;

import com.jose.ticket.domain.board.entity.Comment;
import com.jose.ticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 👇 writer와 board를 함께 즉시 로딩
    @EntityGraph(attributePaths = {"writer", "board"})
    List<Comment> findByBoardId(Long boardId);

    List<Comment> findByWriter(User user);
}
