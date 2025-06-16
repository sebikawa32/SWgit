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

    /**
     * 인증 코드 전송 (회원가입, 비밀번호 재설정 등 목적별)
     * @param toEmail 수신자 이메일
     * @param purpose "SIGN_UP" | "RESET_PASSWORD"
     */
    public void sendAuthCode(String toEmail, String purpose) {
        System.out.println("📤 인증 코드 발송 요청: " + purpose);
        System.out.println("📧 대상 이메일: " + toEmail);
        System.out.println("🕒 서버 현재 시간 (UTC): " + OffsetDateTime.now(ZoneOffset.UTC));

        String code = String.format("%06d", new Random().nextInt(999999));

        EmailAuthCode authCode = EmailAuthCode.builder()
                .email(toEmail)
                .code(code)
                .purpose(purpose)
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(30))
                .verified(false)
                .build();

        codeRepository.save(authCode);
        System.out.println("✅ DB 저장 완료 - 인증코드: " + code);

        // 이메일 제목 및 본문 설정
        String subject = purpose.equals("RESET_PASSWORD") ?
                "[티켓플래닛] 비밀번호 재설정 인증 코드입니다" :
                "[티켓플래닛] 이메일 인증 코드입니다";

        String text = purpose.equals("RESET_PASSWORD") ?
                "비밀번호를 재설정하시려면 아래 인증번호를 입력해주세요: " + code :
                "회원가입을 완료하려면 아래 인증번호를 입력해주세요: " + code;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("📨 이메일 전송 성공");
        } catch (Exception e) {
            System.err.println("📧 이메일 전송 실패: " + e.getMessage());
            throw new RuntimeException("이메일 전송 실패");
        }
    }

    /**
     * 인증 코드 검증
     * @param email 이메일
     * @param inputCode 사용자 입력 코드
     * @param purpose 목적 (회원가입/비밀번호 재설정)
     * @return 성공 여부
     */
    public boolean verifyCode(String email, String inputCode, String purpose) {
        System.out.println("✅ 인증 코드 검증 시도");
        System.out.println("📧 이메일: " + email);
        System.out.println("🔑 입력 코드: " + inputCode);
        System.out.println("📌 목적: " + purpose);

        Optional<EmailAuthCode> optional = codeRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose);

        if (optional.isEmpty()) {
            System.out.println("❌ 인증 코드 없음 (DB 조회 실패)");
            return false;
        }

        EmailAuthCode saved = optional.get();

        System.out.println("📄 DB 저장된 코드: " + saved.getCode());
        System.out.println("⏰ 만료시간: " + saved.getExpiresAt());
        System.out.println("🔓 인증여부(isVerified): " + saved.isVerified());
        System.out.println("🕒 현재시간(UTC): " + OffsetDateTime.now(ZoneOffset.UTC));

        boolean valid = !saved.isVerified()
                && saved.getCode().equals(inputCode)
                && saved.getExpiresAt().isAfter(OffsetDateTime.now(ZoneOffset.UTC));

        System.out.println("🔍 유효성 결과: " + valid);

        if (valid) {
            saved.setVerified(true);
            codeRepository.save(saved);
            System.out.println("✅ 인증 완료 처리 (DB 반영)");
        }

        return valid;
    }

    /**
     * 특정 목적에 대한 이메일 인증 여부 확인
     */
    public boolean isEmailVerified(String email, String purpose) {
        System.out.println("🔍 이메일 인증 여부 확인");
        System.out.println("📧 이메일: " + email);
        System.out.println("📌 목적: " + purpose);

        return codeRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .map(code -> {
                    System.out.println("✔ 최근 인증 여부: " + code.isVerified());
                    return code.isVerified();
                })
                .orElseGet(() -> {
                    System.out.println("❌ 인증 기록 없음");
                    return false;
                });
    }
}
