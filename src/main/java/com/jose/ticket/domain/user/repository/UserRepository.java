package com.jose.ticket.domain.user.repository;

//repository - DB에 회원 데이터를 저장, 조회, 수정, 삭제 기능 담당

import org.springframework.data.jpa.repository.JpaRepository;
import com.jose.ticket.domain.user.entity.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long>  {
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
   

}
