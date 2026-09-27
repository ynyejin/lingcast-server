package com.lingcast.server.domain.news.dto.external;

import java.time.OffsetDateTime;

public record GNewsArticle(
        String title,
        String description,
        String content,
        String url,
        String image,
        OffsetDateTime publishedAt,
        GNewsSource source
) {
}