package com.jose.ticket.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jose.ticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/** 회원 데이터 접근 인터페이스
  - 기본 CRUD 지원
  - userId, email로 조회 및 존재 여부 확인 메서드 포함 **/

public interface UserRepository extends JpaRepository<User, Long>  {

    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByUserId(@Param("userId") String userId);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);
}
