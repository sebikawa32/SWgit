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
import com.jose.ticket.domain.notification.service.NotificationService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService; // 알림 서비스 DI

    // 댓글 생성 (알림 X)
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

    public void deleteComment(Long id, User loginUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // ✨ 작성자 or 관리자만 삭제 허용
        if (!comment.getWriter().getId().equals(loginUser.getId()) &&
                !loginUser.getRole().equals("ADMIN")) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // 댓글 생성 + 게시글 작성자 알림 (핵심 부분!)
    public CommentResponse createComment(CommentRequest request, Long userId) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("작성자 정보를 찾을 수 없습니다."));

        Board board = postRepository.findById(request.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .writer(writer)    //  연관관계 주입
                .board(board)
                .build();

        Comment savedComment = commentRepository.save(comment);

        // [여기서] 댓글 작성자 ≠ 게시글 작성자일 때만 알림 전송
        Long boardWriterId = board.getWriter().getId();
        if (!boardWriterId.equals(userId)) {
            String nickname = writer.getNickname();
            String message = nickname + "님이 내 게시물에 댓글을 남겼어요: \"" + request.getContent() + "\"";
            notificationService.createNotification(
                    boardWriterId,                   // 알림 받을 유저
                    "COMMENT",                       // 알림 타입
                    message,                         // 알림 내용
                    "/board/" + board.getId(),       // 알림 클릭시 이동 경로(게시글 상세)
                    "POST",                          // 알림 대상 타입
                    board.getId()                    // 알림 대상 PK
            );
        }

        return toDto(savedComment);
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
                .map(this::toDto) //  댓글을 DTO로 변환
                .toList();
    }

    public List<CommentResponse> getCommentsByUser(User user) {
        return commentRepository.findByWriter(user).stream()
                .map(this::toDto)
                .toList();
    }
}
