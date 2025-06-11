package com.jose.ticket.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User implements UserDetails {

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

    // ✅ UserDetails 필수 메서드 구현

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한은 없다고 가정
    }

    @Override
    public String getUsername() {
        return this.userId; // 사용자 식별자 (로그인용)
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 안됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 아님
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 안됨
    }

    @Override
    public boolean isEnabled() {
        return true; // 활성화 상태
    }
}
