package com.jose.ticket.global.security;

import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    private final long validityInMilliseconds = 1000 * 60 * 60 * 24; // 24시간

    private final UserRepository userRepository;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
        log.info("✅ JWT 서명 키 초기화 → {}비트", keyBytes.length * 8);
    }

    /**
     * JWT 토큰 생성 (PK 기준)
     */
    public String createToken(Long userId, String role) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 인증 정보 추출: subject는 반드시 user_id (PK)
     */
    public Authentication getAuthentication(String token) {
        String subject = getUserId(token);
        Long userId;

        try {
            userId = Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new RuntimeException("❌ JWT subject 값이 user_id(Long 타입)이 아닙니다. 잘못된 토큰입니다: " + subject);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다. user_id=" + userId));

        log.info("[JwtProvider] 인증된 User: id={}, userId={}, email={}", user.getId(), user.getUserId(), user.getEmail());

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    /**
     * 토큰에서 subject (user_id) 추출
     */
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 요청 헤더에서 토큰 추출 (Bearer ...)
     */
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith(BEARER_PREFIX))
                ? bearer.substring(BEARER_PREFIX.length())
                : null;
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        if (key == null) {
            log.error("❌ JWT 키가 초기화되지 않았습니다. @PostConstruct 확인 필요");
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("❌ 만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("❌ 지원되지 않는 JWT 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.warn("❌ 잘못된 형식의 JWT 토큰입니다.");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("❌ JWT 토큰이 유효하지 않습니다. 이유: {}", e.getMessage());
        }
        return false;
    }
}
