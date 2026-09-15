package com.lingcast.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // API별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // 회원가입은 로그인하지 않은 사용자도 접근 가능
                        .requestMatchers(
                                "/api/v1/users/signup",
                                "/api/v1/auth/login"
                        ).permitAll()

                        // 그 외 API -> 인증 필요
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}