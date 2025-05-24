package com.jose.ticket.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**TokenResponse DTO
 - 로그인 성공 시 클라이언트에게 반환하는 인증 토큰 정보를 담는 객체
 - 토큰 문자열 하나만 포함**/

@Getter
@AllArgsConstructor
public class TokenResponse {
    // 인증에 사용되는 JWT 또는 기타 토큰 문자열
    private String token;
}
