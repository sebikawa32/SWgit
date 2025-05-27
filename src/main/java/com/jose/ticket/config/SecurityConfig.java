package com.jose.ticket.config;

import com.jose.ticket.global.security.JwtAuthenticationFilter;
import com.jose.ticket.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * SecurityConfig
 * - JWT 인증 및 스프링 시큐리티 설정 담당
 * - CORS 설정 추가하여 프론트엔드와의 통신 허용
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider; // JWT 토큰 생성/검증 담당

    // 비밀번호 암호화용 인코더 등록 (BCrypt 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 매니저 빈 등록 (로그인 인증 처리)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 보안 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // CORS 활성화 (프론트엔드와의 통신 허용)
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (API 토큰 기반 인증에 적합)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 없이 무상태 처리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // 회원 관련 API는 인증 없이 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                ); // JWT 인증 필터를 필터 체인에 추가

        return http.build();
    }

    // CORS 필터 빈 등록 (프론트엔드 주소 허용)
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));  // 프론트엔드 주소 (React 개발서버)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용 HTTP 메서드
        config.setAllowCredentials(true); // 쿠키 인증 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 API 경로에 대해 CORS 적용

        return new CorsFilter(source);
    }

}