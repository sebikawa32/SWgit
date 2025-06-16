package com.jose.ticket.domain.user.controller;

import com.jose.ticket.domain.user.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        emailAuthService.sendVerificationCode(email);
        return ResponseEntity.ok("이메일 전송 완료!");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean result = emailAuthService.verifyCode(email, code);
        return result ? ResponseEntity.ok("인증 성공!") :
                ResponseEntity.badRequest().body("인증 실패: 코드가 틀리거나 만료되었습니다.");
    }

}
