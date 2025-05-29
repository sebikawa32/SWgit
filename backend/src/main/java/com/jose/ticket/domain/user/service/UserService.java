package com.jose.ticket.domain.user.service;

//회원가입 시 비밀번호 암호화, 사용자 중복 검사 등 구현

import com.jose.ticket.domain.user.dto.TokenResponse;
import com.jose.ticket.domain.user.dto.UserResponse;
import com.jose.ticket.global.exception.PasswordMismatchException;
import com.jose.ticket.global.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.jose.ticket.domain.user.dto.UserLoginRequest;
import com.jose.ticket.domain.user.dto.UserSignupRequest;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입 처리
    public UserResponse signup(UserSignupRequest request) {
        // 아이디 중복 체크
        if (userRepository.existsByUserId(request.getUserId()))
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        // 비밀번호 일치 여부 확인
        if (!request.getPassword().equals(request.getPasswordConfirm()))
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다");

        // 사용자 엔티티 생성 및 저장
        User user = User.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .realname(request.getRealname())
                .phoneNumber(request.getPhoneNumber())
                .provider("local")
                .build();

        // 저장 후 UserResponse 반환
        return UserResponse.fromEntity(userRepository.save(user));
    }

    // 로그인 처리
    public TokenResponse login(UserLoginRequest request) {
        // 아이디로 사용자 조회
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");

        // JWT 토큰 생성 후 반환
        return new TokenResponse(jwtProvider.createToken(user.getUserId()));
    }

    // 아이디 중복 여부 확인 메서드 (중복확인 API에서 호출)
    public boolean isUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }
}
