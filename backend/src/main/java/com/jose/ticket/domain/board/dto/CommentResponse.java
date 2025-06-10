package com.jose.ticket.domain.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private Long writerId;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BoardSummary board;  // 게시글 요약 정보 추가

    @Getter
    @Builder
    public static class BoardSummary {
        private Long id;
        private String title;
        private String content;
    }
}
