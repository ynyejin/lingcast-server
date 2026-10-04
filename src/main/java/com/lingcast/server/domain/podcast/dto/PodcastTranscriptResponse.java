package com.lingcast.server.domain.podcast.dto;

import com.lingcast.server.domain.podcast.entity.Podcast;

public record PodcastTranscriptResponse(
        Long podcastId,
        String transcript
) {

    public static PodcastTranscriptResponse from(Podcast podcast) {
        return new PodcastTranscriptResponse(
                podcast.getId(),
                podcast.getScript()
        );
    }
}