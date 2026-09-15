package com.lingcast.server.global.jwt;

import tools.jackson.databind.ObjectMapper;
import com.lingcast.server.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // 인증되지 않은 사용자가 보호된 API에 접근하면 401 응답 반환
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.error(
                "인증이 필요합니다.",
                "UNAUTHORIZED"
        );

        // ApiResponse 객체를 JSON으로 변환하여 응답에 작성
        objectMapper.writeValue(
                response.getWriter(),
                apiResponse
        );
    }
}