package com.lingcast.server.domain.user.dto.request;

import com.lingcast.server.domain.common.EnglishLevel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PreferenceRequest(

        @NotNull(message = "영어 수준은 필수입니다.")
        EnglishLevel englishLevel,

        @NotEmpty(message = "관심 분야를 하나 이상 선택해야 합니다.")
        List<String> categories

) {
}