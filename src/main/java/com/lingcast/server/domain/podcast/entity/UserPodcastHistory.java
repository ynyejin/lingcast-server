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
}