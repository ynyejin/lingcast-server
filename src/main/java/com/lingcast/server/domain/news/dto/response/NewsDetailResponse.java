package com.lingcast.server.domain.news.dto.response;

import com.lingcast.server.domain.news.entity.News;

import java.time.LocalDateTime;

public record NewsDetailResponse(
        Long newsId,
        String title,
        String summary,
        String source,
        String category,
        String originalUrl,
        String imageUrl,
        LocalDateTime publishedAt
) {

    public static NewsDetailResponse from(News news) {
        return new NewsDetailResponse(
                news.getId(),
                news.getTitle(),
                news.getSummary(),
                news.getSource(),
                news.getCategory(),
                news.getOriginalUrl(),
                news.getImageUrl(),
                news.getPublishedAt()
        );
    }
}