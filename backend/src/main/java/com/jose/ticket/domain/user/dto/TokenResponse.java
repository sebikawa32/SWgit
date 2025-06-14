package com.jose.ticket.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TokenResponse DTO
 * - 로그인 성공 시 클라이언트에게 반환하는 인증 토큰 정보를 담는 객체
 * - 토큰, userId, nickname, role 포함
 */
@Getter
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private Long userId;
    private String nickname;
    private String role;
}
