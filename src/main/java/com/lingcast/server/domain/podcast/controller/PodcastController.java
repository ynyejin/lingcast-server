package com.lingcast.server.domain.podcast.controller;

import com.lingcast.server.domain.podcast.client.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/podcasts")
@RequiredArgsConstructor
public class PodcastController {

    private final GeminiClient geminiClient;

    @GetMapping("/test/gemini")
    public String testGemini() {

        // Gemini API 연결 테스트
        return geminiClient.generateText(
                "Write a very short English podcast introduction about technology news."
        );
    }
}