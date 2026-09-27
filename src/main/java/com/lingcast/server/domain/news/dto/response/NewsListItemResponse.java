package com.lingcast.server.domain.news.dto.response;

import com.lingcast.server.domain.news.entity.News;

import java.time.LocalDateTime;

public record NewsListItemResponse(
        Long newsId,
        String title,
        String source,
        String category,
        LocalDateTime publishedAt,
        String summary,
        String imageUrl
) {

    public static NewsListItemResponse from(News news) {
        return new NewsListItemResponse(
                news.getId(),
                news.getTitle(),
                news.getSource(),
                news.getCategory(),
                news.getPublishedAt(),
                news.getSummary(),
                news.getImageUrl()
        );
    }
}