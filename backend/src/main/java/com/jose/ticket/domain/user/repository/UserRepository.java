package com.jose.ticket.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jose.ticket.domain.user.entity.User;

import java.util.Optional;

/** 회원 데이터 접근 인터페이스
  - 기본 CRUD 지원
  - userId, email로 조회 및 존재 여부 확인 메서드 포함 **/

public interface UserRepository extends JpaRepository<User, Long>  {

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);
}
