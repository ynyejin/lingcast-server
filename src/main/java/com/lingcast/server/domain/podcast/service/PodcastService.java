package com.lingcast.server.domain.podcast.service;

import com.lingcast.server.domain.common.EnglishLevel;
import com.lingcast.server.domain.news.entity.News;
import com.lingcast.server.domain.news.repository.NewsRepository;
import com.lingcast.server.domain.podcast.client.GeminiClient;
import com.lingcast.server.domain.podcast.client.GeminiTtsClient;
import com.lingcast.server.domain.podcast.dto.*;
import com.lingcast.server.domain.podcast.entity.Podcast;
import com.lingcast.server.domain.podcast.entity.PodcastNews;
import com.lingcast.server.domain.podcast.entity.UserPodcastHistory;
import com.lingcast.server.domain.podcast.repository.PodcastNewsRepository;
import com.lingcast.server.domain.podcast.repository.PodcastRepository;
import com.lingcast.server.domain.podcast.repository.UserPodcastHistoryRepository;
import com.lingcast.server.domain.user.entity.User;
import com.lingcast.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PodcastService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final GeminiClient geminiClient;
    private final PodcastRepository podcastRepository;
    private final PodcastNewsRepository podcastNewsRepository;
    private final GeminiTtsClient geminiTtsClient;
    private final AudioFileService audioFileService;
    private final UserPodcastHistoryRepository userPodcastHistoryRepository;

    @Transactional
    public PodcastCreateResponse generatePodcast(Long userId, String category) {

        // 로그인한 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        EnglishLevel englishLevel = user.getEnglishLevel();

        // 해당 카테고리의 최신 뉴스 3개 조회
        List<News> newsList =
                newsRepository.findTop3ByCategoryOrderByPublishedAtDesc(category);

        if (newsList.isEmpty()) {
            throw new IllegalArgumentException("팟캐스트를 생성할 뉴스가 없습니다.");
        }

        // Gemini 프롬프트 생성
        String prompt = createPrompt(category, englishLevel, newsList);

        // Gemini로 팟캐스트 스크립트 생성
        String script = geminiClient.generateText(prompt);

        // 팟캐스트 저장
        Podcast podcast = Podcast.create(
                createPodcastTitle(category),
                category,
                englishLevel,
                script
        );

        podcastRepository.save(podcast);

        // 팟캐스트와 사용된 뉴스 연결
        List<PodcastNews> podcastNewsList = newsList.stream()
                .map(news -> PodcastNews.create(podcast, news))
                .toList();

        podcastNewsRepository.saveAll(podcastNewsList);

        try {
            // 생성된 팟캐스트 스크립트를 음성으로 변환
            byte[] pcmAudio =
                    geminiTtsClient.generateSpeech(script);

            // PCM 데이터를 WAV 파일로 저장
            String audioUrl =
                    audioFileService.saveAsWav(
                            podcast.getId(),
                            pcmAudio
                    );

            // 팟캐스트 생성 완료 처리
            podcast.complete(audioUrl, null);

        } catch (Exception e) {

            // TTS 또는 음성 파일 생성 실패 처리
            podcast.fail();

            throw e;
        }

        return PodcastCreateResponse.from(podcast);
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

    private String createPodcastTitle(String category) {
        // 카테고리별 팟캐스트 제목 생성
        return "Today's " + category + " Briefing";
    }

    @Transactional(readOnly = true)
    public PodcastDetailResponse getPodcastDetail(Long podcastId) {

        // 팟캐스트 조회
        Podcast podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 팟캐스트입니다.")
                );

        return PodcastDetailResponse.from(podcast);
    }

    @Transactional(readOnly = true)
    public PodcastListResponse getPodcastList(int page, int size) {

        // 최신 생성된 팟캐스트부터 조회
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<PodcastListItemResponse> podcastPage =
                podcastRepository.findAll(pageable)
                        .map(PodcastListItemResponse::from);

        return PodcastListResponse.from(podcastPage);
    }

    @Transactional(readOnly = true)
    public PodcastTranscriptResponse getPodcastTranscript(Long podcastId) {

        // 팟캐스트 조회
        Podcast podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 팟캐스트입니다.")
                );

        return PodcastTranscriptResponse.from(podcast);
    }

    @Transactional(readOnly = true)
    public PodcastStatusResponse getPodcastStatus(Long podcastId) {

        // 팟캐스트 조회
        Podcast podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 팟캐스트입니다."
                        )
                );

        return PodcastStatusResponse.from(podcast);
    }

    @Transactional
    public void updatePodcastProgress(
            Long userId,
            Long podcastId,
            PodcastProgressRequest request
    ) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );

        // 팟캐스트 조회
        Podcast podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 팟캐스트입니다.")
                );

        // 기존 청취 기록이 있는지 확인
        UserPodcastHistory history =
                userPodcastHistoryRepository
                        .findByUserIdAndPodcastId(userId, podcastId)
                        .orElseGet(() ->
                                UserPodcastHistory.create(
                                        user,
                                        podcast,
                                        request.progressSec()
                                )
                        );

        // 기존 기록이면 재생 위치 업데이트
        if (history.getId() != null) {
            history.updateProgress(request.progressSec());
        }

        userPodcastHistoryRepository.save(history);
    }

    @Transactional
    public void completePodcast(
            Long userId,
            Long podcastId
    ) {

        // 팟캐스트 존재 여부 확인
        if (!podcastRepository.existsById(podcastId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 팟캐스트입니다."
            );
        }

        // 사용자의 청취 기록 조회
        UserPodcastHistory history =
                userPodcastHistoryRepository
                        .findByUserIdAndPodcastId(userId, podcastId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "팟캐스트 청취 기록이 없습니다."
                                )
                        );

        // 청취 완료 처리
        history.complete();
    }
}