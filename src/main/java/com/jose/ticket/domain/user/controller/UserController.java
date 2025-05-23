package com.jose.ticket.domain.user.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//회원가입, 로그인 요청 처리, 적절한 응답 반환

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import com.jose.ticket.global.response.ApiResponse;
import com.jose.ticket.domain.user.dto.TokenResponse;
import com.jose.ticket.domain.user.dto.UserResponse;
import com.jose.ticket.domain.user.dto.UserLoginRequest;
import com.jose.ticket.domain.user.dto.UserSignupRequest;
import com.jose.ticket.domain.user.service.UserService;



import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        UserResponse userResponse = userService.signup(request);
        return ResponseEntity.ok(new ApiResponse("회원가입 성공", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserLoginRequest request) {
        TokenResponse tokenResponse = userService.login(request);
        return ResponseEntity.ok(new ApiResponse("로그인 성공", tokenResponse));
    }
}
