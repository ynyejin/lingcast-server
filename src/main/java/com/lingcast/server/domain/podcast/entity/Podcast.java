package com.lingcast.server.domain.podcast.entity;

import com.lingcast.server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "podcasts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Podcast extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "podcast_id")
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "english_level", nullable = false, length = 20)
    private EnglishLevel englishLevel;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String script;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PodcastStatus status;

    public enum EnglishLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }

    public enum PodcastStatus {
        GENERATING,
        COMPLETED,
        FAILED
    }
}