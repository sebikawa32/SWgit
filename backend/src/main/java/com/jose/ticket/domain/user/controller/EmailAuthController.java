package com.jose.ticket.domain.user.controller;

import com.jose.ticket.domain.user.dto.EmailRequest;
import com.jose.ticket.domain.user.dto.EmailCodeVerifyRequest;
import com.jose.ticket.domain.user.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    // ✅ 회원가입용 이메일 인증 코드 전송
    @PostMapping("/send")
    public ResponseEntity<String> sendSignUpCode(@RequestParam String email) {
        emailAuthService.sendAuthCode(email, "SIGN_UP");
        return ResponseEntity.ok("회원가입용 인증코드 전송 완료!");
    }

    // ✅ 회원가입용 인증 코드 검증
    @PostMapping("/verify")
    public ResponseEntity<String> verifySignUpCode(@RequestParam String email, @RequestParam String code) {
        boolean result = emailAuthService.verifyCode(email, code, "SIGN_UP");
        return result
                ? ResponseEntity.ok("회원가입 인증 성공!")
                : ResponseEntity.badRequest().body("회원가입 인증 실패: 코드가 틀리거나 만료되었습니다.");
    }

    // ✅ 비밀번호 재설정용 인증 코드 전송
    @PostMapping("/reset-password/send")
    public ResponseEntity<String> sendResetPasswordCode(@RequestBody EmailRequest request) {
        emailAuthService.sendAuthCode(request.getEmail(), "RESET_PASSWORD");
        return ResponseEntity.ok("비밀번호 재설정용 인증코드 전송 완료!");
    }

    // ✅ 비밀번호 재설정용 인증 코드 검증
    @PostMapping("/reset-password/verify")
    public ResponseEntity<String> verifyResetPasswordCode(@RequestBody EmailCodeVerifyRequest request) {
        boolean result = emailAuthService.verifyCode(request.getEmail(), request.getCode(), "RESET_PASSWORD");
        return result
                ? ResponseEntity.ok("비밀번호 재설정 인증 성공!")
                : ResponseEntity.badRequest().body("비밀번호 재설정 인증 실패: 코드가 틀리거나 만료되었습니다.");
    }
}
