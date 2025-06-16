package com.jose.ticket.domain.user.repository;

import com.jose.ticket.domain.user.entity.EmailAuthCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthCodeRepository extends JpaRepository<EmailAuthCode, Long> {
    Optional<EmailAuthCode> findTopByEmailOrderByCreatedAtDesc(String email);
}
