package com.jose.ticket.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jose.ticket.domain.user.dto.UserLoginRequest;
import com.jose.ticket.domain.user.dto.UserSignupRequest;
import com.jose.ticket.domain.user.dto.UserResponse;
import com.jose.ticket.domain.user.dto.TokenResponse;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import com.jose.ticket.global.exception.PasswordMismatchException;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public UserResponse signup(UserSignupRequest request) {
        if (userRepository.existsByUserid(request.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다");
        }

        User user = User.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .provider("local")
                .build();

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser); // UserResponse 클래스에 fromEntity 메소드 필요
    }

    // 로그인
    public TokenResponse login(UserLoginRequest request) {
        User user = userRepository.findByUserid(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성 로직 필요 (여기선 임시값)
        String token = "생성된_토큰_값"; // 실제로는 JWT 생성기를 사용하세요.
        return new TokenResponse(token); // TokenResponse는 token을 필드로 가진 DTO여야 함
    }
}
