package com.example.knitting.girls.data.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.addAllowedOriginPattern("*"); // 모든 도메인 허용
                    corsConfiguration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
                    corsConfiguration.addAllowedHeader("*"); // 모든 헤더 허용
                    corsConfiguration.setAllowCredentials(true); // 인증 정보 허용
                    return corsConfiguration;
                }))

                .authorizeHttpRequests(config -> config
                        .requestMatchers("auth//login/callback").permitAll()
                        .anyRequest().permitAll()  // 다른 모든 요청 허용
                )
                .oauth2Login(oauth2Configurer -> oauth2Configurer
                        .loginPage("/login")
                        .successHandler(successHandler()) // 로그인 성공 후 리디렉션
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            response.sendRedirect("/login_success");
        };
    }
}