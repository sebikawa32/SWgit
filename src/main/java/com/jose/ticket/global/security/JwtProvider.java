package com.jose.ticket.global.security;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.JwtException;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secretKey; // application.properties에서 주입받는 JWT 비밀키

    private Key key; // 서명용 키 객체

    private final long validityInMilliseconds = 1000 * 60 * 60 * 24; // 토큰 유효기간 (24시간)

    private final CustomUserDetailsService userDetailsService; // 사용자 정보 로드 서비스

    @PostConstruct
    protected void init() {
        // secretKey를 Base64로 인코딩하여 HMAC-SHA 키 생성
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 토큰 생성 (userId를 subject로 설정)
    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)         // 클레임 설정
                .setIssuedAt(now)           // 발급 시간
                .setExpiration(expiry)      // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘과 키 지정
                .compact();                 // 토큰 생성
    }

    // 토큰에서 인증 정보 추출하여 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String userId = getUserId(token);
        var userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 userId(subject) 추출
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // HTTP 요청 헤더에서 "Authorization" 토큰 추출 (Bearer 접두어 제거)
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith(BEARER_PREFIX))
                ? bearer.substring(BEARER_PREFIX.length())
                : null;
    }

    // 토큰 유효성 검증 (만료, 서명, 형식 등 체크)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.", e);
        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 JWT 토큰입니다.", e);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 토큰이 유효하지 않습니다.", e);
        }
        return false;
    }
}
