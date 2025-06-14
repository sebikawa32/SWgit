package com.jose.ticket.global.security;

import com.jose.ticket.domain.user.entity.User;
import com.jose.ticket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/** CustomUserDetailsService 클래스
 - Spring Security에서 사용자 인증 정보를 가져오는 서비스
 - UserRepository를 사용해 DB에서 사용자 정보를 조회
 - UserDetails 객체로 변환하여 인증 처리에 사용 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자 아이디로 DB에서 사용자 조회
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        return new CustomUserDetails(user);  // ✅ 커스텀 유저 디테일 반환
    }

}