package com.lingcast.server.global.config;

import com.lingcast.server.global.jwt.JwtAuthenticationEntryPoint;
import com.lingcast.server.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // API별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // 회원가입은 로그인하지 않은 사용자도 접근 가능
                        .requestMatchers(
                                "/api/v1/users/signup",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh"
                        ).permitAll()
                        // 그 외 API는 인증 필요
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        // 인증되지 않은 요청의 401 응답을 공통 형식으로 처리
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // JWT 인증 필터를 Spring Security 필터 체인에 등록
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}