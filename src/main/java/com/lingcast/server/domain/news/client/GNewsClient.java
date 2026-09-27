package com.lingcast.server.domain.news.client;

import com.lingcast.server.domain.news.dto.external.GNewsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GNewsClient {

    private final RestClient restClient;

    public GNewsClient(
            RestClient.Builder restClientBuilder,
            @Value("${gnews.base-url}") String baseUrl,
            @Value("${gnews.api-key}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", apiKey)
                .build();
    }

    public GNewsResponse getTopHeadlines(String category) {

        // GNews의 영어 Top Headlines 조회
        return restClient
                .get()
                .uri(
                        "/top-headlines?category={category}&lang=en&max=10",
                        category
                )
                .retrieve()
                .body(GNewsResponse.class);
    }
}