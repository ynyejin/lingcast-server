package com.lingcast.server.domain.podcast.repository;

import com.lingcast.server.domain.podcast.entity.PodcastNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PodcastNewsRepository extends JpaRepository<PodcastNews, Long> {
}