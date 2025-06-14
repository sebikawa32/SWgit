package com.jose.ticket.domain.board.controller;

import com.jose.ticket.domain.board.dto.CommentRequest;
import com.jose.ticket.domain.board.dto.CommentResponse;
import com.jose.ticket.domain.board.service.CommentService;
import com.jose.ticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // ✅ 댓글 작성 (로그인 사용자만 가능)
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

    // ✅ 댓글 삭제 (작성자 or 관리자만 가능, 권한 체크는 Service에서)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build(); // 인증 안 됨
        }

        commentService.deleteComment(id, user); // ✨ Service에서 권한 체크
        return ResponseEntity.noContent().build();
    }

    // ✅ 댓글 전체 조회 (비회원도 가능)
    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam Long boardId) {
        return ResponseEntity.ok(commentService.getCommentDtosByBoardId(boardId));
    }
}
