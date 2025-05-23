package com.jose.ticket.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import com.jose.ticket.global.response.ApiResponse;
import com.jose.ticket.domain.user.dto.TokenResponse;
import com.jose.ticket.domain.user.dto.UserResponse;
import com.jose.ticket.domain.user.dto.UserLoginRequest;
import com.jose.ticket.domain.user.dto.UserSignupRequest;
import com.jose.ticket.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

// UserController 클래스
// 회원가입과 로그인 API 요청을 처리하는 REST 컨트롤러
// 클라이언트로부터 전달받은 회원가입/로그인 요청을 검증 후
// UserService를 통해 비즈니스 로직을 수행하고,
// 처리 결과를 ApiResponse 형식으로 감싸서 응답 반환


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService; // 회원 관련 서비스 주입

    // 회원가입 API 처리
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        UserResponse userResponse = userService.signup(request); // 회원가입 수행
        return ResponseEntity.ok(new ApiResponse("회원가입 성공", userResponse));
    }

    // 로그인 API 처리
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserLoginRequest request) {
        TokenResponse tokenResponse = userService.login(request); // 로그인 수행
        return ResponseEntity.ok(new ApiResponse("로그인 성공", tokenResponse));
    }
}

