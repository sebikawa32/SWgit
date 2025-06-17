package com.jose.ticket.domain.user.service;

import com.jose.ticket.domain.user.dto.*;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import com.jose.ticket.global.exception.PasswordMismatchException;
import com.jose.ticket.global.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
                .providerId(null)
                .role("USER")
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

        return new TokenResponse(
                token,
                user.getId(),
                user.getNickname(),
                user.getRole()
        );
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

    // 비밀번호 재설정
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다."));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    // ✅ 사용자 프로필 수정
    @Transactional
    public void updateMyProfile(String userId, UserProfileDto dto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        user.setNickname(dto.getNickname());
        user.setRealname(dto.getRealname());
        user.setPhoneNumber(dto.getPhoneNumber());
    }

    // ✅ 사용자 프로필 조회
    @Transactional
    public UserProfileDto getMyProfile(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return UserProfileDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .realname(user.getRealname())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    // ✅ 비밀번호 변경 + 유효성 검사
    @Transactional
    public void changePassword(String userId, ChangePasswordDto dto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (!isValidPassword(dto.getNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 8자 이상이며 영문과 숫자, 특수문자를 포함해야 합니다.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    // ✅ 회원 탈퇴
    @Transactional
    public void deleteAccount(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    // ✅ 비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        return password != null &&
                password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).{8,}$");
    }
}
