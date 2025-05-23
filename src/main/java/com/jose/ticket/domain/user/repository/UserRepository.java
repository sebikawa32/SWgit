package com.jose.ticket.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jose.ticket.domain.user.entity.User;

import java.util.Optional;

// UserRepository
// 회원 데이터 접근 인터페이스
// 기본 CRUD 제공 및 userId, email 조회/존재 여부 확인
public interface UserRepository extends JpaRepository<User, Long>  {

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);
}