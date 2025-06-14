package com.jose.ticket.global.security;

import com.jose.ticket.domain.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("🔵 [JwtFilter] 요청 URI: " + uri);

        // ✅ 1. [여기에 추가] GPT 대화형 검색 요청은 인증 생략
        if (request.getMethod().equals("POST") && uri.equals("/api/chat/search")) {
            System.out.println("🔓 [GPT 검색] 인증 생략 처리됨");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 인증이 필요 없는 공개 API 예외 처리
        if (request.getMethod().equals("GET") && (
                uri.startsWith("/api/comments") ||
                        uri.startsWith("/api/boards") ||
                        uri.startsWith("/api/tickets") ||
                        uri.startsWith("/api/search")
        )) {
            System.out.println("🔓 공개 API 요청 → 인증 건너뜀");
            filterChain.doFilter(request, response);
            return;
        }

        if (uri.equals("/api/users/login") ||
                uri.equals("/api/users/signup") ||
                uri.equals("/api/users/check-id")) {
            System.out.println("🔓 사용자 인증 제외 경로 → 통과");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ JWT 검증
        String token = jwtProvider.resolveToken(request);
        System.out.println("🪪 Authorization 헤더로부터 추출한 토큰: " + token);

        if (token != null) {
            try {
                if (jwtProvider.validateToken(token)) {
                    Authentication auth = jwtProvider.getAuthentication(token);
                    if (auth != null) {
                        Object principal = auth.getPrincipal();
                        System.out.println("✅ 토큰 유효 → SecurityContext 등록 완료");
                        if (principal instanceof User user) {
                            System.out.println("🆔 사용자 ID: " + user.getUserId());
                        }
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        setUnauthorizedResponse(response, "인증 정보를 생성할 수 없습니다.");
                        return;
                    }
                } else {
                    setUnauthorizedResponse(response, "토큰이 유효하지 않거나 만료되었습니다. 다시 로그인해주세요.");
                    return;
                }
            } catch (Exception e) {
                setUnauthorizedResponse(response, "토큰 검증 중 오류가 발생했습니다.");
                return;
            }
        }

        // ✅ 토큰이 없으면 그냥 통과 → SecurityConfig가 인증 여부 판단
        filterChain.doFilter(request, response);
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
