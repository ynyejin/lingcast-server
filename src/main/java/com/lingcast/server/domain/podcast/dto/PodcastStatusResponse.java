package com.lingcast.server.domain.podcast.dto;

import com.lingcast.server.domain.podcast.entity.Podcast;

public record PodcastStatusResponse(
        Long podcastId,
        Podcast.PodcastStatus status
) {

    public static PodcastStatusResponse from(Podcast podcast) {
        return new PodcastStatusResponse(
                podcast.getId(),
                podcast.getStatus()
        );
    }
}