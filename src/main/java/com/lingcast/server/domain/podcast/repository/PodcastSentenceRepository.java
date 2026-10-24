
package com.lingcast.server.domain.podcast.repository;

import com.lingcast.server.domain.podcast.entity.PodcastSentence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PodcastSentenceRepository
        extends JpaRepository<PodcastSentence, Long> {

    List<PodcastSentence> findByPodcastIdOrderBySentenceIndexAsc(
            Long podcastId
    );
}
