package com.jose.ticket.domain.user.service;

import com.jose.ticket.domain.user.entity.EmailAuthCode;
import com.jose.ticket.domain.user.repository.EmailAuthCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender mailSender;
    private final EmailAuthCodeRepository codeRepository;

    public void sendVerificationCode(String toEmail) {
        // 서버 시간 로그 출력
        System.out.println("📤 인증 코드 발송 요청");
        System.out.println("🕒 서버 현재 시간 (UTC): " + OffsetDateTime.now(ZoneOffset.UTC));
        System.out.println("🕒 서버 현재 시간 (로컬): " + OffsetDateTime.now());

        String code = String.format("%06d", new Random().nextInt(999999));

        EmailAuthCode authCode = EmailAuthCode.builder()
                .email(toEmail)
                .code(code)
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(30)) // UTC 기준 30분 후
                .verified(false)
                .build();

        codeRepository.save(authCode);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("티켓플래닛 이메일 인증 코드입니다");
            message.setText("인증 코드: " + code);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("📧 이메일 전송 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage());
        }
    }

    public boolean verifyCode(String email, String inputCode) {
        System.out.println("✅ 인증 코드 검증 시도");
        System.out.println("🕒 서버 현재 시간 (UTC): " + OffsetDateTime.now(ZoneOffset.UTC));
        System.out.println("🕒 서버 현재 시간 (로컬): " + OffsetDateTime.now());

        Optional<EmailAuthCode> optional = codeRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (optional.isEmpty()) {
            System.out.println("❌ 해당 이메일로 발송된 인증 코드 없음");
            return false;
        }

        EmailAuthCode saved = optional.get();

        System.out.println("📬 DB에 저장된 코드: " + saved.getCode());
        System.out.println("📬 DB 만료 시간 (expiresAt): " + saved.getExpiresAt());
        System.out.println("📬 DB verified 여부: " + saved.isVerified());

        boolean valid = !saved.isVerified()
                && saved.getCode().equals(inputCode)
                && saved.getExpiresAt().isAfter(OffsetDateTime.now(ZoneOffset.UTC));

        System.out.println("✅ 인증 코드 유효성 결과: " + valid);

        if (valid) {
            saved.setVerified(true);
            codeRepository.save(saved);
            System.out.println("🔐 인증 완료 및 verified=true 업데이트 완료");
        }

        return valid;
    }

    public boolean isEmailVerified(String email) {
        return codeRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .map(EmailAuthCode::isVerified)
                .orElse(false);
    }
}
