package com.lingcast.server.domain.podcast.repository;

import com.lingcast.server.domain.podcast.entity.UserPodcastHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPodcastHistoryRepository
        extends JpaRepository<UserPodcastHistory, Long> {

    // 사용자와 팟캐스트의 기존 청취 기록 조회
    Optional<UserPodcastHistory> findByUserIdAndPodcastId(
            Long userId,
            Long podcastId
    );
}