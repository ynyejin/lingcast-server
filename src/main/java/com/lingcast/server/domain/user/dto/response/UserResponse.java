package com.lingcast.server.domain.user.dto.response;

import com.lingcast.server.domain.common.EnglishLevel;
import com.lingcast.server.domain.user.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String email,
        String nickname,
        EnglishLevel englishLevel,
        LocalDateTime createdAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getEnglishLevel(),
                user.getCreatedAt()
        );
    }
}