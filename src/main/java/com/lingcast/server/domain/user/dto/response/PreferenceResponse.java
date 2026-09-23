package com.lingcast.server.domain.user.dto.response;

import com.lingcast.server.domain.common.EnglishLevel;

import java.util.List;

public record PreferenceResponse(
        EnglishLevel englishLevel,
        List<String> categories
) {

    public static PreferenceResponse of(
            EnglishLevel englishLevel,
            List<String> categories
    ) {
        return new PreferenceResponse(englishLevel, categories);
    }
}