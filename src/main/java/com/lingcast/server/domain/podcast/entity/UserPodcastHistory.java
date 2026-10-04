package com.lingcast.server.domain.podcast.entity;

import com.lingcast.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_podcast_history",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_podcast_history",
                        columnNames = {"user_id", "podcast_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPodcastHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "podcast_id", nullable = false)
    private Podcast podcast;

    @Column(name = "progress_sec", nullable = false)
    private Integer progressSec = 0;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(name = "last_listened_at")
    private LocalDateTime lastListenedAt;

    // 팟캐스트 청취 기록 생성
    public static UserPodcastHistory create(
            User user,
            Podcast podcast,
            Integer progressSec
    ) {
        UserPodcastHistory history = new UserPodcastHistory();

        history.user = user;
        history.podcast = podcast;
        history.progressSec = progressSec;
        history.completed = false;
        history.lastListenedAt = LocalDateTime.now();

        return history;
    }

    // 팟캐스트 재생 위치 업데이트
    public void updateProgress(Integer progressSec) {
        this.progressSec = progressSec;
        this.lastListenedAt = LocalDateTime.now();
    }

    // 팟캐스트 청취 완료 처리
    public void complete() {
        this.completed = true;
        this.lastListenedAt = LocalDateTime.now();
    }
}