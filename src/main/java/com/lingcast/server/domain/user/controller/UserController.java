package com.lingcast.server.domain.user.controller;

import com.lingcast.server.domain.user.dto.request.SignupRequest;
import com.lingcast.server.domain.user.dto.response.SignupResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.lingcast.server.domain.user.dto.response.UserResponse;
import com.lingcast.server.domain.user.service.UserService;
import com.lingcast.server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {

        SignupResponse response = userService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        response,
                        "회원가입이 완료되었습니다."
                ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(
            @AuthenticationPrincipal Long userId
    ) {
        // JWT 인증 정보에서 현재 로그인한 사용자 ID를 받아 회원 정보 조회
        UserResponse response = userService.getMyInfo(userId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "내 정보 조회에 성공했습니다.")
        );
    }
}