package com.lingcast.server.domain.podcast.dto;

import com.lingcast.server.domain.common.EnglishLevel;
import com.lingcast.server.domain.podcast.entity.Podcast;

public record PodcastCreateResponse(
        Long podcastId,
        String title,
        String category,
        EnglishLevel englishLevel,
        Podcast.PodcastStatus status
) {

    public static PodcastCreateResponse from(Podcast podcast) {
        return new PodcastCreateResponse(
                podcast.getId(),
                podcast.getTitle(),
                podcast.getCategory(),
                podcast.getEnglishLevel(),
                podcast.getStatus()
        );
    }
}