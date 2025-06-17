package com.jose.ticket.domain.user.controller;

import com.jose.ticket.domain.user.dto.*;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.service.EmailAuthService;
import com.jose.ticket.domain.user.service.UserService;
import com.jose.ticket.global.response.ApiResponse;
import com.jose.ticket.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;
    private final EmailAuthService emailAuthService;

    /** 회원가입 API **/
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        boolean isVerified = emailAuthService.isEmailVerified(request.getEmail(), "SIGN_UP");
        if (!isVerified) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("이메일 인증이 완료되지 않았습니다.", null));
        }

        UserResponse userResponse = userService.signup(request);
        return ResponseEntity.ok(new ApiResponse("회원가입 성공", userResponse));
    }

    /** 로그인 API **/
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserLoginRequest request) {
        TokenResponse tokenResponse = userService.login(request);
        return ResponseEntity.ok(new ApiResponse("로그인 성공", tokenResponse));
    }

    /** 아이디 중복확인 API **/
    @PostMapping("/check-id")
    public ResponseEntity<ApiResponse> checkUserId(@RequestBody String userId) {
        boolean exists = userService.isUserIdExists(userId);
        return ResponseEntity.ok(new ApiResponse("아이디 중복검사 결과", exists));
    }

    /** 내 정보 조회 API **/
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMyInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("인증된 사용자 정보가 없습니다.", null));
        }
        UserResponse userResponse = userService.findById(user.getId());
        return ResponseEntity.ok(new ApiResponse("내 정보 조회 성공", userResponse));
    }

    /** 내 정보 수정 API **/
    @PutMapping("/me")
    public ResponseEntity<ApiResponse> updateMyInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfileDto dto) {
        userService.updateMyProfile(user.getUserId(), dto);
        return ResponseEntity.ok(new ApiResponse("사용자 정보 수정 성공", null));
    }

    /** 비밀번호 변경 API **/
    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordDto dto) {
        userService.changePassword(user.getUserId(), dto);
        return ResponseEntity.ok(new ApiResponse("비밀번호 변경 성공", null));
    }

    /** 회원 탈퇴 API **/
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteMyAccount(@AuthenticationPrincipal User user) {
        userService.deleteAccount(user.getUserId());
        return ResponseEntity.ok(new ApiResponse("회원 탈퇴 성공", null));
    }

    /** 사용자 ID로 조회 API - 인증 및 권한 체크 포함 **/
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || !userDetails.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("권한이 없습니다.", null));
        }

        UserResponse userResponse = userService.findById(userId);
        return ResponseEntity.ok(new ApiResponse("사용자 조회 성공", userResponse));
    }

    /** 비밀번호 재설정 API **/
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        boolean verified = emailAuthService.isEmailVerified(request.getEmail(), "RESET_PASSWORD");
        if (!verified) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("이메일 인증이 완료되지 않았습니다.", null));
        }

        userService.updatePassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse("비밀번호가 성공적으로 변경되었습니다.", null));
    }
}