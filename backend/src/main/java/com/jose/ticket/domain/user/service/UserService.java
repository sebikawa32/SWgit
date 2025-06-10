package com.jose.ticket.domain.user.service;

import com.jose.ticket.domain.user.dto.TokenResponse;
import com.jose.ticket.domain.user.dto.UserResponse;
import com.jose.ticket.global.exception.PasswordMismatchException;
import com.jose.ticket.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jose.ticket.domain.user.dto.UserLoginRequest;
import com.jose.ticket.domain.user.dto.UserSignupRequest;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입 처리
    public UserResponse signup(UserSignupRequest request) {
        if (userRepository.existsByUserId(request.getUserId()))
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        if (!request.getPassword().equals(request.getPasswordConfirm()))
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다");

        User user = User.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .realname(request.getRealname())
                .phoneNumber(request.getPhoneNumber())
                .provider("local")
                .build();

        return UserResponse.fromEntity(userRepository.save(user));
    }

    // 로그인 처리
    public TokenResponse login(UserLoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");

        String token = jwtProvider.createToken(user.getUserId());

        return new TokenResponse(token, user.getId());
    }

    // 아이디 중복 확인
    public boolean isUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 사용자 ID로 조회 (프로필 조회)
    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));
        return UserResponse.fromEntity(user);
    }
}
