
package com.lingcast.server.domain.podcast.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "podcast_sentences",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_podcast_sentence_index",
                columnNames = {"podcast_id", "sentence_index"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PodcastSentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sentence_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "podcast_id", nullable = false)
    private Podcast podcast;

    @Column(name = "sentence_index", nullable = false)
    private Integer sentenceIndex;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String english;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String korean;

    @Column(name = "start_ms", nullable = false)
    private Long startMs;

    @Column(name = "end_ms", nullable = false)
    private Long endMs;

    public static PodcastSentence create(
            Podcast podcast,
            Integer sentenceIndex,
            String english,
            String korean,
            Long startMs,
            Long endMs
    ) {
        if (podcast == null) {
            throw new IllegalArgumentException(
                    "팟캐스트는 필수입니다."
            );
        }

        if (sentenceIndex == null || sentenceIndex < 0) {
            throw new IllegalArgumentException(
                    "문장 순서는 0 이상이어야 합니다."
            );
        }

        if (english == null || english.isBlank()) {
            throw new IllegalArgumentException(
                    "영어 문장은 필수입니다."
            );
        }

        if (startMs == null || endMs == null
                || startMs < 0 || endMs <= startMs) {
            throw new IllegalArgumentException(
                    "문장 시작 및 종료 시간이 올바르지 않습니다."
            );
        }

        PodcastSentence sentence = new PodcastSentence();

        sentence.podcast = podcast;
        sentence.sentenceIndex = sentenceIndex;
        sentence.english = english;
        sentence.korean = korean;
        sentence.startMs = startMs;
        sentence.endMs = endMs;

        return sentence;
    }
}
