package com.lingcast.server.domain.news.repository;

import com.lingcast.server.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {

    // 같은 원문 URL의 뉴스가 이미 저장되어 있는지 확인
    boolean existsByOriginalUrl(String originalUrl);
}