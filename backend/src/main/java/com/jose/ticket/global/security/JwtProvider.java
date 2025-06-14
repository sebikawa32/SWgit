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
import java.util.Base64;
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
        try {
            byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
            key = Keys.hmacShaKeyFor(keyBytes);
            log.info("✅ JWT 서명 키 초기화 완료");
        } catch (Exception e) {
            log.error("❌ JWT 키 초기화 실패: {}", e.getMessage());
        }
    }

    /** JWT 토큰 생성 */
    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 인증 정보 추출 */
    public Authentication getAuthentication(String token) {
        String userId = getUserId(token);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다: " + userId));
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    /** 사용자 ID 추출 */
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** 요청 헤더에서 토큰 추출 */
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith(BEARER_PREFIX))
                ? bearer.substring(BEARER_PREFIX.length())
                : null;
    }

    /** 토큰 유효성 검증 */
    public boolean validateToken(String token) {
        if (key == null) {
            log.error("❌ validateToken() 실패: key가 null입니다. @PostConstruct 호출 여부 확인 필요!");
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
