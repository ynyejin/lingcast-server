package com.lingcast.server.domain.podcast.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PodcastListResponse(
        List<PodcastListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static PodcastListResponse from(
            Page<PodcastListItemResponse> page
    ) {
        return new PodcastListResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}