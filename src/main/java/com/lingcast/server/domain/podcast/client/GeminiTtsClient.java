package com.lingcast.server.domain.podcast.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class GeminiTtsClient {

    private final RestClient restClient;

    public GeminiTtsClient(
            RestClient.Builder restClientBuilder,
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
    }

    public byte[] generateSpeech(String text) {

        // Gemini TTS 요청 데이터 생성
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of(
                                                "text", text,
                                                "speech_metadata", Map.of(
                                                        "style",
                                                        "Clear and friendly English news podcast host"
                                                )
                                        )
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "responseModalities", List.of("AUDIO"),
                        "speechConfig", Map.of(
                                "voiceConfig", Map.of(
                                        "voice", "Kore"
                                )
                        )
                )
        );

        Map response = restClient
                .post()
                .uri("/v1beta/models/gemini-3.8-flash-tts:generateContent")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return extractAudio(response);
    }

    private byte[] extractAudio(Map response) {

        // Gemini 응답에서 Base64 오디오 데이터 추출
        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.get("candidates");

        Map<String, Object> content =
                (Map<String, Object>) candidates.get(0).get("content");

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

        Map<String, Object> inlineData =
                (Map<String, Object>) parts.get(0).get("inlineData");

        String base64Audio = (String) inlineData.get("data");

        return Base64.getDecoder().decode(base64Audio);
    }
}