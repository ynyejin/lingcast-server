package com.lingcast.server.domain.auth.controller;

import com.lingcast.server.domain.auth.service.AuthService;
import com.lingcast.server.domain.user.dto.request.LoginRequest;
import com.lingcast.server.domain.user.dto.response.LoginResponse;
import com.lingcast.server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lingcast.server.domain.user.dto.request.RefreshTokenRequest;
import com.lingcast.server.domain.user.dto.response.RefreshTokenResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        // 이메일과 비밀번호를 검증하고 JWT를 발급
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "로그인에 성공했습니다."
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        // 유효한 Refresh Token으로 새로운 Access Token 발급
        RefreshTokenResponse response = authService.refresh(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Access Token이 재발급되었습니다."
                )
        );
    }
}