package com.lingcast.server.domain.podcast.service;

import com.lingcast.server.domain.common.EnglishLevel;
import com.lingcast.server.domain.news.entity.News;
import com.lingcast.server.domain.news.repository.NewsRepository;
import com.lingcast.server.domain.podcast.client.GeminiClient;
import com.lingcast.server.domain.user.entity.User;
import com.lingcast.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PodcastService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final GeminiClient geminiClient;

    public String generatePodcastScript(Long userId, String category) {

        // 로그인한 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 사용자의 영어 레벨 조회
        EnglishLevel englishLevel = user.getEnglishLevel();

        // 해당 카테고리의 최신 뉴스 3개 조회
        List<News> newsList =
                newsRepository.findTop3ByCategoryOrderByPublishedAtDesc(category);

        if (newsList.isEmpty()) {
            throw new IllegalArgumentException("팟캐스트를 생성할 뉴스가 없습니다.");
        }

        // 사용자 영어 레벨을 포함한 프롬프트 생성
        String prompt = createPrompt(category, englishLevel, newsList);

        return geminiClient.generateText(prompt);
    }

    private String createPrompt(
            String category,
            EnglishLevel englishLevel,
            List<News> newsList
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
            You are a script writer for LingCast,
            an English-learning news podcast.

            Create one short English podcast script using the news stories below.

            Category: %s
            English Level: %s

            Requirements:
            - Combine the news stories into one natural podcast episode.
            - Introduce each story separately.
            - Adjust vocabulary and sentence complexity to the English level.
            - BEGINNER: Use simple vocabulary and short sentences.
            - INTERMEDIATE: Use natural everyday news English.
            - ADVANCED: Use more sophisticated vocabulary and sentence structures.
            - Do not invent facts that are not provided.
            - Start with a short LingCast introduction.
            - End with a short closing.

            """.formatted(category, englishLevel));

        for (int i = 0; i < newsList.size(); i++) {

            News news = newsList.get(i);

            prompt.append("""
                News %d
                Title: %s
                Summary: %s

                """.formatted(
                    i + 1,
                    news.getTitle(),
                    news.getSummary()
            ));
        }

        return prompt.toString();
    }
}