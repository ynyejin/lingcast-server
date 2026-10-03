package com.lingcast.server.domain.podcast.dto;

import com.lingcast.server.domain.common.EnglishLevel;
import com.lingcast.server.domain.podcast.entity.Podcast;

public record PodcastListItemResponse(
        Long podcastId,
        String title,
        String category,
        EnglishLevel englishLevel,
        Integer durationSec,
        Podcast.PodcastStatus status
) {

    public static PodcastListItemResponse from(Podcast podcast) {
        return new PodcastListItemResponse(
                podcast.getId(),
                podcast.getTitle(),
                podcast.getCategory(),
                podcast.getEnglishLevel(),
                podcast.getDurationSec(),
                podcast.getStatus()
        );
    }
}