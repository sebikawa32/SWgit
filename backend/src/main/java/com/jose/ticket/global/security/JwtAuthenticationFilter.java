package com.jose.ticket.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter 클래스
 * - 모든 HTTP 요청마다 실행되는 필터
 * - 요청 헤더에서 JWT 토큰을 추출하고 유효성 검사 수행
 * - 유효한 토큰이면 Authentication 객체를 SecurityContext에 저장하여 인증 처리
 * - 단, 로그인/회원가입 요청은 JWT 검사에서 제외
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider; // JWT 토큰 처리 클래스

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /** 실제 필터링 로직 수행 메서드 **/
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 로그인, 회원가입 요청은 JWT 검사 제외
        if (uri.equals("/api/users/login") || uri.equals("/api/users/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 JWT 토큰 추출
        String token = jwtProvider.resolveToken(request);

        // 토큰이 존재하고 유효하면 인증 정보 SecurityContext에 저장
        if (token != null && jwtProvider.validateToken(token)) {
            var auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }
}
