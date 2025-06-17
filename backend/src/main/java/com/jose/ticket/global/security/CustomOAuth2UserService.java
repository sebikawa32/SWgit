package com.jose.ticket.global.security;

import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // DB에 사용자 없으면 자동 회원가입
        userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 유니크 userId 생성 (임시 UUID 사용)
                    String generatedUserId = "google_" + UUID.randomUUID().toString().substring(0, 8);

                    User newUser = User.builder()
                            .userId(generatedUserId)      // 일반 회원가입 userId와 충돌없도록 prefix 붙임
                            .email(email)
                            .nickname(name != null ? name : generatedUserId)
                            .role("USER")                // 권한은 USER 고정
                            .provider("GOOGLE")          // 구글 로그인 표시
                            .build();
                    return userRepository.save(newUser);
                });

        return oAuth2User;
    }
}
