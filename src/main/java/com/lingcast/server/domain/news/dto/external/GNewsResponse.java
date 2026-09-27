package com.lingcast.server.domain.news.dto.external;

import java.util.List;

public record GNewsResponse(
        int totalArticles,
        List<GNewsArticle> articles
) {
}