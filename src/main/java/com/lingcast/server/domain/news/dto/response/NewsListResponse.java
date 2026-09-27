package com.lingcast.server.domain.news.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record NewsListResponse(
        List<NewsListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static NewsListResponse from(Page<NewsListItemResponse> newsPage) {
        return new NewsListResponse(
                newsPage.getContent(),
                newsPage.getNumber(),
                newsPage.getSize(),
                newsPage.getTotalElements(),
                newsPage.getTotalPages()
        );
    }
}