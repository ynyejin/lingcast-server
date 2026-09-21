package com.lingcast.server.domain.user.dto.response;

import com.lingcast.server.domain.user.entity.User;

public record UpdateUserResponse(
        Long userId,
        String nickname
) {

    public static UpdateUserResponse from(User user) {
        return new UpdateUserResponse(
                user.getId(),
                user.getNickname()
        );
    }
}