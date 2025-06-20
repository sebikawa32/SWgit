package com.jose.ticket.config;

import com.jose.ticket.global.security.JwtAuthenticationFilter;
import com.jose.ticket.global.security.JwtProvider;
import com.jose.ticket.global.security.CustomOAuth2UserService;
import com.jose.ticket.global.security.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ actuator 하위 전체 예외 처리 (헬스체크/프로메테우스 등 모두 포함)
                        .requestMatchers("/actuator/**").permitAll()

                        // 인증 없이 접근 허용할 경로
                        .requestMatchers(
                                "/api/users/signup",
                                "/api/users/login",
                                "/api/users/check-id",
                                "/api/users/reset-password",
                                "/api/tickets/**",
                                "/api/search",
                                "/api/search/**",
                                "/api/keywords/popular/**",
                                "/api/keywords/popular",
                                "/api/bookmarks/count",
                                "/api/boards", "/api/boards/", "/api/boards?**", "/api/boards/tickets/**",
                                "/api/chat/**",
                                "/api/auth/email/send",
                                "/api/auth/email/verify",
                                "/api/auth/email/reset-password/**",
                                "/api/notifications/**",
                                "/api/alerts/**",
                                "/api/alerts",
                                "/api/test/dday",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/api/auth/google-login"
                        ).permitAll()

                        // GET 게시글 단건 조회 허용
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/boards/**").permitAll()
                        // GET 댓글 조회 허용
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/comments/**").permitAll()
                        // POST 댓글 작성
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/comments/**").authenticated()
                        // 게시글 작성/수정/삭제
                        .requestMatchers("/api/boards/**").authenticated()
                        // 나머지 요청 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
