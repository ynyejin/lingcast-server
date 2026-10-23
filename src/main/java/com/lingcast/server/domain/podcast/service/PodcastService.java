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
    public PodcastCreateResponse generatePodcast(
            Long userId,
            Long newsId
    ) {

        // 1. 로그인한 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 사용자입니다."
                        )
                );

        EnglishLevel englishLevel = user.getEnglishLevel();

        // 2. 선택한 뉴스 조회
        News news = newsRepository.findById(newsId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 뉴스입니다."
                        )
                );

        // 3. 이미 생성 완료된 팟캐스트가 있다면 재사용
        Podcast existingPodcast =
                podcastNewsRepository.findCompletedPodcast(
                        newsId,
                        englishLevel
                ).orElse(null);

        if (existingPodcast != null) {
            return PodcastCreateResponse.from(existingPodcast);
        }

        // 4. 선택한 뉴스로 Gemini 프롬프트 생성
        String prompt = createPrompt(
                englishLevel,
                news
        );

        // 5. 영어 팟캐스트 스크립트 생성
        String script = geminiClient.generateText(prompt);

        // 6. 팟캐스트 저장
        Podcast podcast = Podcast.create(
                news.getTitle(),
                news.getCategory(),
                englishLevel,
                script
        );

        podcastRepository.save(podcast);

        // 7. 원본 뉴스와 팟캐스트 연결
        PodcastNews podcastNews =
                PodcastNews.create(podcast, news);

        podcastNewsRepository.save(podcastNews);

        try {

            // 8. Gemini TTS로 음성 생성
            byte[] pcmAudio =
                    geminiTtsClient.generateSpeech(script);

            // 9. WAV 파일 저장
            String audioUrl =
                    audioFileService.saveAsWav(
                            podcast.getId(),
                            pcmAudio
                    );

            // 10. 생성 완료 처리
            podcast.complete(audioUrl, null);

        } catch (Exception e) {

            podcast.fail();
            throw e;
        }

        return PodcastCreateResponse.from(podcast);
    }

    private String createPrompt(
            EnglishLevel englishLevel,
            News news
    ) {

        return """
                You are a script writer for LingCast,
                an English-learning news podcast.

                Create one short English podcast script
                based on the news article below.

                English Level: %s

                Requirements:
                - Focus only on the provided news story.
                - Explain the news naturally and accurately.
                - Adjust vocabulary and sentence complexity
                  to the English level.
                - BEGINNER: Use simple vocabulary and short sentences.
                - INTERMEDIATE: Use natural everyday news English.
                - ADVANCED: Use sophisticated vocabulary and
                  sentence structures.
                - Do not invent facts that are not provided.
                - Start with a short LingCast introduction.
                - End with a short closing.
                - Output only the English podcast script.

                News Title: %s
                News Summary: %s
                """.formatted(
                englishLevel,
                news.getTitle(),
                news.getSummary()
        );
    }

    @Transactional(readOnly = true)
    public PodcastDetailResponse getPodcastDetail(Long podcastId) {

        Podcast podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 팟캐스트입니다."
                        )
                );

        return PodcastDetailResponse.from(podcast);
    }

    @Transactional(readOnly = true)
    public PodcastListResponse getPodcastList(
            int page,
            int size
    ) {

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
    public PodcastTranscriptResponse getPodcastTranscript(
            Long podcastId
    ) {

        Podcast podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 팟캐스트입니다."
                        )
                );

        return PodcastTranscriptResponse.from(podcast);
    }

    @Transactional(readOnly = true)
    public PodcastStatusResponse getPodcastStatus(
            Long podcastId
    ) {

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

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 사용자입니다."
                        )
                );

        Podcast podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 팟캐스트입니다."
                        )
                );

        UserPodcastHistory history =
                userPodcastHistoryRepository
                        .findByUserIdAndPodcastId(
                                userId,
                                podcastId
                        )
                        .orElseGet(() ->
                                UserPodcastHistory.create(
                                        user,
                                        podcast,
                                        request.progressSec()
                                )
                        );

        if (history.getId() != null) {
            history.updateProgress(
                    request.progressSec()
            );
        }

        userPodcastHistoryRepository.save(history);
    }

    @Transactional
    public void completePodcast(
            Long userId,
            Long podcastId
    ) {

        if (!podcastRepository.existsById(podcastId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 팟캐스트입니다."
            );
        }

        UserPodcastHistory history =
                userPodcastHistoryRepository
                        .findByUserIdAndPodcastId(
                                userId,
                                podcastId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "팟캐스트 청취 기록이 없습니다."
                                )
                        );

        history.complete();
    }
}