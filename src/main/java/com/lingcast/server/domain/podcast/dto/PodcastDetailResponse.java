package com.lingcast.server.domain.podcast.dto;

import com.lingcast.server.domain.common.EnglishLevel;
import com.lingcast.server.domain.podcast.entity.Podcast;

public record PodcastDetailResponse(
        Long podcastId,
        String title,
        String category,
        EnglishLevel englishLevel,
        String script,
        String audioUrl,
        Integer durationSec,
        Podcast.PodcastStatus status
) {

    public static PodcastDetailResponse from(Podcast podcast) {
        return new PodcastDetailResponse(
                podcast.getId(),
                podcast.getTitle(),
                podcast.getCategory(),
                podcast.getEnglishLevel(),
                podcast.getScript(),
                podcast.getAudioUrl(),
                podcast.getDurationSec(),
                podcast.getStatus()
        );
    }
}