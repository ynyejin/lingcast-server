package com.lingcast.server.domain.podcast.controller;

import com.lingcast.server.domain.podcast.dto.*;
import com.lingcast.server.domain.podcast.service.PodcastService;
import com.lingcast.server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/podcasts")
@RequiredArgsConstructor
public class PodcastController {

    private final PodcastService podcastService;

    @PostMapping
    public ApiResponse<PodcastCreateResponse> createPodcast(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long newsId
    ) {
        PodcastCreateResponse response =
                podcastService.generatePodcast(userId, newsId);

        return ApiResponse.success(
                response,
                "팟캐스트 생성에 성공했습니다."
        );
    }

    @GetMapping("/{podcastId}")
    public ApiResponse<PodcastDetailResponse> getPodcastDetail(
            @PathVariable Long podcastId
    ) {
        PodcastDetailResponse response =
                podcastService.getPodcastDetail(podcastId);

        return ApiResponse.success(
                response,
                "팟캐스트 상세 조회에 성공했습니다."
        );
    }

    @GetMapping
    public ApiResponse<PodcastListResponse> getPodcastList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PodcastListResponse response =
                podcastService.getPodcastList(page, size);

        return ApiResponse.success(
                response,
                "팟캐스트 목록 조회에 성공했습니다."
        );
    }

    @GetMapping("/{podcastId}/transcript")
    public ApiResponse<PodcastTranscriptResponse> getPodcastTranscript(
            @PathVariable Long podcastId
    ) {
        PodcastTranscriptResponse response =
                podcastService.getPodcastTranscript(podcastId);

        return ApiResponse.success(
                response,
                "팟캐스트 Transcript 조회에 성공했습니다."
        );
    }

    @GetMapping("/{podcastId}/status")
    public ApiResponse<PodcastStatusResponse> getPodcastStatus(
            @PathVariable Long podcastId
    ) {
        PodcastStatusResponse response =
                podcastService.getPodcastStatus(podcastId);

        return ApiResponse.success(
                response,
                "팟캐스트 생성 상태 조회에 성공했습니다."
        );
    }

    @PatchMapping("/{podcastId}/progress")
    public ApiResponse<Void> updatePodcastProgress(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long podcastId,
            @Valid @RequestBody PodcastProgressRequest request
    ) {
        podcastService.updatePodcastProgress(
                userId,
                podcastId,
                request
        );

        return ApiResponse.success(
                null,
                "팟캐스트 재생 위치 저장에 성공했습니다."
        );
    }

    @PostMapping("/{podcastId}/complete")
    public ApiResponse<Void> completePodcast(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long podcastId
    ) {
        podcastService.completePodcast(userId, podcastId);

        return ApiResponse.success(
                null,
                "팟캐스트 청취 완료 처리에 성공했습니다."
        );
    }
}