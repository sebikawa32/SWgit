package com.jose.ticket.domain.user.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.jose.ticket.domain.user.dto.*;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import com.jose.ticket.domain.user.service.UserService;
import com.jose.ticket.global.response.ApiResponse;
import com.jose.ticket.global.security.JwtProvider;
import com.jose.ticket.global.util.GoogleTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        log.info("📥 구글 로그인 요청 도착: idToken = {}", request.getIdToken());

        Payload payload = GoogleTokenVerifier.verify(request.getIdToken());

        if (payload == null) {
            log.warn("❌ 구글 ID 토큰 검증 실패");
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String subject = payload.getSubject(); // Google 고유 사용자 ID

        log.info("✅ 검증된 이메일: {}, 이름: {}, subject: {}", email, name, subject);

        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;

        if (existingUser.isPresent()) {
            User foundUser = existingUser.get();
            String provider = foundUser.getProvider();

            log.info("🔍 기존 유저의 provider: {}", provider);

            if (provider == null || !provider.equals("GOOGLE")) {
                log.warn("⚠️ 이미 일반 또는 다른 OAuth 회원으로 가입된 이메일: {}", email);
                return ResponseEntity.status(409).body("이미 일반 회원으로 가입된 이메일입니다.");
            }

            log.info("🔄 구글 연동 기존 유저 로그인 허용: {}", email);
            user = foundUser;
        } else {
            log.info("🆕 신규 유저 등록: {}", email);
            user = User.builder()
                    .email(email)
                    .nickname(name)
                    .provider("GOOGLE")
                    .providerId(subject)
                    .role("USER")
                    .build();
            user = userRepository.save(user);
        }

        String token = jwtProvider.createToken(user.getId(), user.getRole());

        GoogleLoginResponse response = new GoogleLoginResponse(
                token,
                user.getId(),
                user.getRole(),
                user.getNickname(),
                user.getProvider()
        );

        log.info("🎫 JWT 토큰 발급 완료: {}", token);
        log.info("📦 응답 데이터: token={}, userId={}, role={}, nickname={}, provider={}",
                token, user.getId(), user.getRole(), user.getNickname(), user.getProvider());

        return ResponseEntity.ok(response);
    }

    // 4단계: 구글 추가정보 저장 API
    @PostMapping("/google-signup")
    public ResponseEntity<ApiResponse> googleSignup(@Valid @RequestBody GoogleSignupRequest request) {
        userService.updateGoogleUserAdditionalInfo(request);
        return ResponseEntity.ok(new ApiResponse("추가 정보 저장 완료", null));
    }
}
