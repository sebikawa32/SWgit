package com.jose.ticket.domain.user.entity;  
// 이 파일이 속한 패키지 경로를 지정. 보통 도메인(user) 안에 entity를 둠.


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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // DB 컬럼명: user_id
    private Long id; // 엔티티 내 기본 키 (PK)
	
	@Column(name = "user_userid", unique = true)
	private String userId; // 로컬 로그인용 ID (선택사항)

    @Column(name = "user_email", nullable = false, unique = true)
    private String email; // 이메일 (필수, 중복 불가)

    @Column(name = "user_password")
    private String password; // 비밀번호 (로컬 로그인 시 사용)

    @Column(name = "user_nickname")
    private String nickname; // 사용자 닉네임

    @Column(name = "user_provider", nullable = false)
    private String provider; // 로그인 방식 구분: "local", "kakao", "naver" 등

    @Column(name = "user_provider_id", unique = true)
    private String providerId; // 소셜 로그인 제공자의 고유 사용자 ID

}