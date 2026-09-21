package com.lingcast.server.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
        String nickname

) {
}