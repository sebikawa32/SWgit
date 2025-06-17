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

    // 회원가입 처리 - userId는 유저가 직접 지정하므로 여전히 체크 가능
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

    // 로그인 처리 - PK(id) 기준으로 토큰 생성
    public TokenResponse login(UserLoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");

        String token = jwtProvider.createToken(user.getId(), user.getRole());

        return new TokenResponse(
                token,
                user.getId(),
                user.getNickname(),
                user.getRole()
        );
    }

    // 아이디 중복 확인은 여전히 userId 기준
    public boolean isUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 사용자 프로필 조회 - PK(id) 기준으로 조회
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + id));
        return UserResponse.fromEntity(user);
    }

    // 비밀번호 재설정 (email 기준)
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다."));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    // 사용자 프로필 수정 - PK(id) 기준
    @Transactional
    public void updateMyProfile(Long id, UserProfileDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        user.setNickname(dto.getNickname());
        user.setRealname(dto.getRealname());
        user.setPhoneNumber(dto.getPhoneNumber());
    }

    // 사용자 프로필 조회 - PK(id) 기준
    @Transactional
    public UserProfileDto getMyProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return UserProfileDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .realname(user.getRealname())
                .phoneNumber(user.getPhoneNumber())
                .provider(user.getProvider())
                .build();
    }

    // 비밀번호 변경 - PK(id) 기준
    @Transactional
    public void changePassword(Long id, ChangePasswordDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (!isValidPassword(dto.getNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 8자 이상이며 영문과 숫자, 특수문자를 포함해야 합니다.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    // 회원 탈퇴 - PK(id) 기준
    @Transactional
    public void deleteAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    // 비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        return password != null &&
                password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).{8,}$");
    }

    // 구글 로그인 추가정보 수정 (PK id 기준)
    @Transactional
    public void updateGoogleUserAdditionalInfo(GoogleSignupRequest request) {
        User user = userRepository.findById(request.getId())  // 여기서 getId() 호출
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + request.getId()));

        user.setNickname(request.getNickname());
        user.setRealname(request.getRealname());
        user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);
    }
}
