package com.lingcast.server.domain.user.dto.response;

public record RefreshTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {

    // 새로 발급한 Access Token 정보를 응답 형식으로 변환
    public static RefreshTokenResponse of(
            String accessToken,
            long expiresIn
    ) {
        return new RefreshTokenResponse(
                accessToken,
                "Bearer",
                expiresIn
        );
    }
}