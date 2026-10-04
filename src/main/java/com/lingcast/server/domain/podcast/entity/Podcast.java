package com.lingcast.server.domain.podcast.entity;

import com.lingcast.server.domain.common.EnglishLevel;
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

    // 생성된 팟캐스트 음성 파일 주소
    @Column(name = "audio_url", length = 1000)
    private String audioUrl;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PodcastStatus status;

    public enum PodcastStatus {
        GENERATING,
        COMPLETED,
        FAILED
    }

    // 새로운 팟캐스트 생성
    public static Podcast create(
            String title,
            String category,
            EnglishLevel englishLevel,
            String script
    ) {
        Podcast podcast = new Podcast();
        podcast.title = title;
        podcast.category = category;
        podcast.englishLevel = englishLevel;
        podcast.script = script;
        podcast.status = PodcastStatus.GENERATING;

        return podcast;
    }

    // 팟캐스트 음성 생성 완료
    public void complete(String audioUrl, Integer durationSec) {
        this.audioUrl = audioUrl;
        this.durationSec = durationSec;
        this.status = PodcastStatus.COMPLETED;
    }

    // 팟캐스트 음성 생성 실패
    public void fail() {
        this.status = PodcastStatus.FAILED;
    }
}