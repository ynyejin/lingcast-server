package com.lingcast.server.domain.podcast.repository;

import com.lingcast.server.domain.podcast.entity.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PodcastRepository extends JpaRepository<Podcast, Long> {
}