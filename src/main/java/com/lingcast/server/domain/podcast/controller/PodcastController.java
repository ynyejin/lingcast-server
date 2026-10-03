package com.lingcast.server.domain.podcast.controller;

import com.lingcast.server.domain.podcast.client.GeminiClient;
import com.lingcast.server.domain.podcast.service.PodcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/podcasts")
@RequiredArgsConstructor
public class PodcastController {

    private final PodcastService podcastService;

    @GetMapping("/test/generate")
    public String testGeneratePodcast(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "IT") String category
    ) {
        // 로그인 사용자의 영어 레벨에 맞는 스크립트 생성
        return podcastService.generatePodcastScript(userId, category);
    }
}