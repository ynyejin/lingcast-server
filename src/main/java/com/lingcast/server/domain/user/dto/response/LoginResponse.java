package com.lingcast.server.domain.user.dto.response;

import com.lingcast.server.domain.user.entity.User;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        long refreshTokenExpiresIn,
        UserInfo user
) {

    // 로그인 성공 시 사용자 정보와 발급된 토큰을 응답 DTO로 변환
    public static LoginResponse of(
            String accessToken,
            String refreshToken,
            User user,
            long expiresIn,
            long refreshTokenExpiresIn
    ) {
        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn,
                refreshTokenExpiresIn,
                UserInfo.from(user)
        );
    }

    public record UserInfo(
            Long userId,
            String email,
            String nickname
    ) {

        // User 엔티티를 API 응답에 필요한 사용자 정보로 변환
        public static UserInfo from(User user) {
            return new UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname()
            );
        }
    }
}