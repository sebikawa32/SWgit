package com.jose.ticket.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // 테이블과 매핑
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // PK

    @Column(name = "user_userid", unique = true)
    private String userId; // 로그인 아이디

    @Column(name = "user_email", nullable = false, unique = true)
    private String email; // 이메일 (필수)

    @Column(name = "user_password")
    private String password; // 비밀번호

    @Column(name = "user_nickname")
    private String nickname; // 닉네임

    @Column(name = "user_realname")
    private String realname; // 실명

    @Column(name = "user_phone_number", unique = true)
    private String phoneNumber; // 휴대폰 번호

    @Column(name = "user_provider", nullable = false)
    private String provider; // 로그인 제공자

    @Column(name = "user_provider_id", unique = true)
    private String providerId; // 제공자 고유 ID

}
