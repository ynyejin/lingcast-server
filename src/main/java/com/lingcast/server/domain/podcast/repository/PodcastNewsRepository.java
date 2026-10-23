package com.lingcast.server.domain.podcast.repository;

import com.lingcast.server.domain.common.EnglishLevel;
import com.lingcast.server.domain.podcast.entity.Podcast;
import com.lingcast.server.domain.podcast.entity.PodcastNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PodcastNewsRepository extends JpaRepository<PodcastNews, Long> {

    // 뉴스 ID와 영어 레벨에 맞는 완료된 팟캐스트 조회
    @Query("""
        SELECT pn.podcast
        FROM PodcastNews pn
        WHERE pn.news.id = :newsId
          AND pn.podcast.englishLevel = :englishLevel
          AND pn.podcast.status = :status
        ORDER BY pn.podcast.id DESC
        """)
    java.util.List<Podcast> findPodcastsByNewsIdAndEnglishLevelAndStatus(
            @Param("newsId") Long newsId,
            @Param("englishLevel") EnglishLevel englishLevel,
            @Param("status") Podcast.PodcastStatus status
    );

    // 해당 뉴스의 기존 팟캐스트가 있는지 조회
    default Optional<Podcast> findCompletedPodcast(
            Long newsId,
            EnglishLevel englishLevel
    ) {
        return findPodcastsByNewsIdAndEnglishLevelAndStatus(
                newsId,
                englishLevel,
                Podcast.PodcastStatus.COMPLETED
        ).stream().findFirst();
    }
}