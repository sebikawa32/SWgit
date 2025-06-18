package com.jose.ticket.domain.board.controller;

import com.jose.ticket.domain.board.dto.CommentRequest;
import com.jose.ticket.domain.board.dto.CommentResponse;
import com.jose.ticket.domain.board.service.CommentService;
import com.jose.ticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * ✅ 댓글 작성
     * 로그인한 사용자만 가능하며, 인증된 User 객체에서 userId를 추출하여 저장합니다.
     */
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build(); // 인증 안 됨
        }

        Long userId = user.getId();
        return ResponseEntity.ok(commentService.createComment(request, userId));
    }

    /**
     * ✅ 댓글 삭제
     * 작성자 또는 관리자인 경우에만 삭제 허용
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build(); // 인증 안 됨
        }

        commentService.deleteComment(id, user); // Service에서 권한 검증
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 특정 게시글의 모든 댓글 조회
     * Query 파라미터로 boardId를 받아 해당 게시글의 댓글 리스트 반환
     * 비회원도 접근 가능
     */
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@RequestParam Long boardId) {
        return ResponseEntity.ok(commentService.getCommentDtosByBoardId(boardId));
    }

    /**
     * ✅ 내가 작성한 댓글 목록 조회
     * 로그인한 사용자에 한해 조회 가능
     */
    @GetMapping("/my-comments")
    public ResponseEntity<List<CommentResponse>> getMyComments(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build(); // 인증 실패 시
        }

        List<CommentResponse> comments = commentService.getCommentsByUser(user);
        return ResponseEntity.ok(comments);
    }
}
