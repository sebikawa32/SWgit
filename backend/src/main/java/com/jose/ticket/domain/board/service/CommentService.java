package com.jose.ticket.domain.board.service;

import com.jose.ticket.domain.board.dto.CommentRequest;
import com.jose.ticket.domain.board.dto.CommentResponse;
import com.jose.ticket.domain.board.entity.Board;
import com.jose.ticket.domain.board.entity.Comment;
import com.jose.ticket.domain.board.repository.CommentRepository;
import com.jose.ticket.domain.board.repository.PostRepository;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 댓글 생성
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // 특정 게시글 댓글 전체 조회
    public List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    // 댓글 ID로 조회
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    // 댓글 삭제
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public CommentResponse createComment(CommentRequest request, Long userId) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("작성자 정보를 찾을 수 없습니다."));

        Board board = postRepository.findById(request.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .writer(writer)    // ✅ 연관관계 주입
                .board(board)
                .build();

        return toDto(commentRepository.save(comment));
    }
    private CommentResponse toDto(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .writerId(comment.getWriter().getId())
                .nickname(comment.getWriter().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .board(CommentResponse.BoardSummary.builder()
                        .id(comment.getBoard().getId())
                        .title(comment.getBoard().getTitle())
                        .content(comment.getBoard().getContent())
                        .build())
                .build();
    }

    public List<CommentResponse> getCommentDtosByBoardId(Long boardId) {
        return commentRepository.findByBoardId(boardId).stream()
                .map(this::toDto) // ✅ 댓글을 DTO로 변환
                .toList();
    }

}
