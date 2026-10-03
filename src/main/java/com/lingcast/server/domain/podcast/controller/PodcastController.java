package com.lingcast.server.domain.podcast.controller;

import com.lingcast.server.domain.podcast.dto.PodcastCreateResponse;
import com.lingcast.server.domain.podcast.service.PodcastService;
import com.lingcast.server.global.response.ApiResponse;
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
}