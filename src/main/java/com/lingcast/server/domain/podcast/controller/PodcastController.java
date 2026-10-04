package com.lingcast.server.domain.podcast.controller;

import com.lingcast.server.domain.podcast.client.GeminiTtsClient;
import com.lingcast.server.domain.podcast.dto.*;
import com.lingcast.server.domain.podcast.service.PodcastService;
import com.lingcast.server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/podcasts")
@RequiredArgsConstructor
public class PodcastController {

    private final PodcastService podcastService;
    private final GeminiTtsClient geminiTtsClient;

    @PostMapping
    public ApiResponse<PodcastCreateResponse> createPodcast(
            @AuthenticationPrincipal Long userId,
            @RequestParam String category
    ) {
        // 로그인 사용자의 영어 레벨에 맞는 팟캐스트 생성
        PodcastCreateResponse response =
                podcastService.generatePodcast(userId, category);

        return ApiResponse.success(
                response,
                "팟캐스트 생성에 성공했습니다."
        );
    }

    @GetMapping("/{podcastId}")
    public ApiResponse<PodcastDetailResponse> getPodcastDetail(
            @PathVariable Long podcastId
    ) {
        // 팟캐스트 상세 정보 조회
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
        // 팟캐스트 목록 조회
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
        // 팟캐스트 Transcript 조회
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

        // 팟캐스트 생성 상태 조회
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

        // 사용자의 팟캐스트 재생 위치 저장
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
}