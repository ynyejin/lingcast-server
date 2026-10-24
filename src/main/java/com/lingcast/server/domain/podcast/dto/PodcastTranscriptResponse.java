
package com.lingcast.server.domain.podcast.dto;

import com.lingcast.server.domain.podcast.entity.Podcast;
import com.lingcast.server.domain.podcast.entity.PodcastSentence;

import java.util.List;

public record PodcastTranscriptResponse(
        Long podcastId,
        String transcript,
        List<SentenceResponse> sentences
) {

    public record SentenceResponse(
            Integer sentenceIndex,
            String english,
            String korean,
            Long startMs,
            Long endMs
    ) {

        public static SentenceResponse from(PodcastSentence sentence) {
            return new SentenceResponse(
                    sentence.getSentenceIndex(),
                    sentence.getEnglish(),
                    sentence.getKorean(),
                    sentence.getStartMs(),
                    sentence.getEndMs()
            );
        }
    }

    public static PodcastTranscriptResponse from(
            Podcast podcast,
            List<PodcastSentence> sentences
    ) {

        List<SentenceResponse> sentenceResponses =
                sentences.stream()
                        .map(SentenceResponse::from)
                        .toList();

        return new PodcastTranscriptResponse(
                podcast.getId(),
                podcast.getScript(),
                sentenceResponses
        );
    }
}
