package com.jose.ticket.config;

import com.jose.ticket.global.security.AwsSecretsManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final AwsSecretsManager secretsManager;

    @Bean
    public JavaMailSender javaMailSender() {
        // 🔐 Secrets Manager에서 이메일 정보 가져오기
        Map<String, String> secrets = secretsManager.getSecretMap("ticketplanet/credentials");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(secrets.get("MAIL_USERNAME"));
        mailSender.setPassword(secrets.get("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
