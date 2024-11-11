package com.example.knitting.girls.data.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // AuthenticationManager 빈 선언
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    // HTTP 보안 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login/**", "/callback").permitAll()  // 로그인 페이지와 콜백 URL은 인증 없이 접근 가능
                                .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                )
                .addFilter(new JwtAuthenticationFilter(authenticationManager(http)));  // JWT 인증 필터 추가

        return http.build();
    }
}