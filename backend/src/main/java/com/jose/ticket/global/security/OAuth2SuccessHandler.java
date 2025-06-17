package com.jose.ticket.global.security;

import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // OAuth2User → 이메일 추출
        String email = ((org.springframework.security.oauth2.core.user.DefaultOAuth2User) authentication.getPrincipal())
                .getAttribute("email");

        System.out.println("[OAuth2SuccessHandler] 로그인 성공한 사용자 이메일: " + email);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            System.out.println("[OAuth2SuccessHandler] DB에 사용자가 존재하지 않습니다: " + email);
            response.sendRedirect("/login?error=NoUser");
            return;
        }

        User user = optionalUser.get();

        // JWT 토큰 생성 (userId는 Long 타입, role은 String)
        String token = jwtProvider.createToken(user.getId(), user.getRole());
        System.out.println("[OAuth2SuccessHandler] 발급된 JWT 토큰: " + token);

        // 추가 정보 입력 페이지로 리디렉션 (토큰 포함)
        String redirectUrl = "http://localhost:3000/google-signup?token=" + token;
        System.out.println("[OAuth2SuccessHandler] 리디렉션 URL: " + redirectUrl);

        response.sendRedirect(redirectUrl);
    }
}
