package com.jose.ticket.domain.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BoardResponse {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> comments;
}
