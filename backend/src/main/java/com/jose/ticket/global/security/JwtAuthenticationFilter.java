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

        // ✅ 댓글 조회는 인증 생략
        if (request.getMethod().equals("GET") && uri.startsWith("/api/comments")) {
            System.out.println("🔕 댓글 GET 요청 → 인증 건너뜀");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 로그인/회원가입 관련 인증 생략
        if (uri.equals("/api/users/login") ||
                uri.equals("/api/users/signup") ||
                uri.equals("/api/users/check-id")) {
            System.out.println("🔓 인증 제외 경로 → 필터 통과");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ JWT 검증 로직 시작
        String token = jwtProvider.resolveToken(request);
        System.out.println("🪪 Authorization 헤더로부터 추출한 토큰: " + token);

        if (token != null) {
            try {
                if (jwtProvider.validateToken(token)) {
                    Authentication auth = jwtProvider.getAuthentication(token);
                    if (auth != null) {
                        Object principal = auth.getPrincipal();
                        System.out.println("✅ 토큰 유효 → SecurityContext에 인증 등록 완료");
                        System.out.println("👤 Principal 클래스: " + principal.getClass().getSimpleName());
                        if (principal instanceof User user) {
                            System.out.println("🆔 인증된 사용자 ID: " + user.getUserId());
                        }
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        System.out.println("❌ 인증 정보 생성 실패 (auth == null)");
                    }
                } else {
                    System.out.println("❌ 토큰 유효성 검사 실패 → validateToken == false");
                }
            } catch (Exception e) {
                System.out.println("❌ 예외 발생 during validateToken/getAuthentication: " + e.getMessage());
            }
        } else {
            System.out.println("❌ 토큰 없음 (resolveToken == null)");
        }

        filterChain.doFilter(request, response);
    }
}
