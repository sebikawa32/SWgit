package com.jose.ticket.domain.user.controller;

import com.jose.ticket.domain.user.dto.*;
import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.service.EmailAuthService;
import com.jose.ticket.global.response.ApiResponse;
import com.jose.ticket.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.jose.ticket.domain.user.service.UserService;

/**
 * UserController
 * - 회원가입(signup), 로그인(login), 아이디 중복확인(check-id)
 * - 내 정보 조회(me) : 인증된 사용자만 접근 가능
 * - 사용자 ID로 조회(getUserById) : 인증 및 권한 체크 포함
 */
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

        // ✅ 이메일 인증 여부 확인
        boolean isVerified = emailAuthService.isEmailVerified(request.getEmail(), "SIGN_UP");
        if (!isVerified) {
            return ResponseEntity
                    .badRequest()
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
    public ResponseEntity<ApiResponse> getMyInfo(
            @AuthenticationPrincipal User user
    ) {
        if  (user == null){
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse("인증된 사용자 정보가 없습니다.", null));
        }

        Long authenticatedUserId = user.getId();
        UserResponse userResponse = userService.findById(authenticatedUserId);
        return ResponseEntity.ok(new ApiResponse("내 정보 조회 성공", userResponse));
    }

    /** 사용자 ID로 조회 API - 인증 및 권한 체크 포함 **/
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !userDetails.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("권한이 없습니다.", null));
        }

        UserResponse userResponse = userService.findById(userId);
        return ResponseEntity.ok(new ApiResponse("사용자 조회 성공", userResponse));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        // 1. 인증 확인
        boolean verified = emailAuthService.isEmailVerified(request.getEmail(), "RESET_PASSWORD");
        if (!verified) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("이메일 인증이 완료되지 않았습니다.", null));
        }

        // 2. 비밀번호 변경
        userService.updatePassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse("비밀번호가 성공적으로 변경되었습니다.", null));
    }


}
