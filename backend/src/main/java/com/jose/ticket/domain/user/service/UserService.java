package com.jose.ticket.domain.user.service;

import com.jose.ticket.domain.user.dto.*;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import com.jose.ticket.domain.bookmark.repository.BookmarkRepository; // ✅ 추가
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
    private final BookmarkRepository bookmarkRepository; // ✅ 주입
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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

    public boolean isUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + id));
        return UserResponse.fromEntity(user);
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다."));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    @Transactional
    public void updateMyProfile(Long id, UserProfileDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        user.setNickname(dto.getNickname());
        user.setRealname(dto.getRealname());
        user.setPhoneNumber(dto.getPhoneNumber());
    }

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

    /** ✅ 회원 탈퇴 - 관련 북마크 먼저 삭제 후 유저 삭제 **/
    @Transactional
    public void deleteAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        bookmarkRepository.deleteByUserId(id);  // 🔥 북마크 먼저 삭제
        userRepository.delete(user);            // 🔥 그 후 유저 삭제
    }

    private boolean isValidPassword(String password) {
        return password != null &&
                password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).{8,}$");
    }

    @Transactional
    public void updateGoogleUserAdditionalInfo(GoogleSignupRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + request.getId()));

        user.setNickname(request.getNickname());
        user.setRealname(request.getRealname());
        user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);
    }

    public void disconnectGoogle(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 🔒 일반 로그인 정보가 없는 경우 → 계정 삭제
        boolean isGoogleOnlyAccount =
                user.getProvider() != null && user.getProvider().equalsIgnoreCase("google")
                        && user.getUserId() == null
                        && user.getPassword() == null;

        if (isGoogleOnlyAccount) {
            // 즐겨찾기 등 연관 엔티티 먼저 정리 필요 시 처리
            userRepository.delete(user);
            return;
        }

        // 🔄 일반 계정 정보가 있는 경우 → 단순 연동 해제만 수행
        user.setProvider(null);
        user.setProviderId(null);
        userRepository.save(user);
    }
}
