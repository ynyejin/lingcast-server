package com.lingcast.server.domain.news.service;

import com.lingcast.server.domain.interest.entity.UserInterest;
import com.lingcast.server.domain.interest.repository.UserInterestRepository;
import com.lingcast.server.domain.news.client.GNewsClient;
import com.lingcast.server.domain.news.dto.external.GNewsArticle;
import com.lingcast.server.domain.news.dto.external.GNewsResponse;
import com.lingcast.server.domain.news.entity.News;
import com.lingcast.server.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lingcast.server.domain.news.dto.response.NewsListItemResponse;
import com.lingcast.server.domain.news.dto.response.NewsListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.lingcast.server.domain.news.dto.response.NewsDetailResponse;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final GNewsClient gNewsClient;
    private final NewsRepository newsRepository;
    private final UserInterestRepository userInterestRepository;

    @Transactional
    public int collectNews(String category) {

        // LingCast 카테고리를 GNews 카테고리로 변환
        String gNewsCategory = convertCategory(category);

        GNewsResponse response = gNewsClient.getTopHeadlines(gNewsCategory);

        if (response == null || response.articles() == null) {
            return 0;
        }

        int savedCount = 0;

        for (GNewsArticle article : response.articles()) {

            // 이미 저장된 뉴스는 건너뜀
            if (newsRepository.existsByOriginalUrl(article.url())) {
                continue;
            }

            LocalDateTime publishedAt =
                    article.publishedAt().toLocalDateTime();

            News news = News.create(
                    article.title(),
                    article.description(),
                    article.source().name(),
                    article.url(),
                    article.image(),
                    category,
                    publishedAt
            );

            newsRepository.save(news);
            savedCount++;
        }

        return savedCount;
    }

    private String convertCategory(String category) {
        return switch (category) {
            case "IT" -> "technology";
            case "경제" -> "business";
            case "과학" -> "science";
            default -> throw new IllegalArgumentException("지원하지 않는 뉴스 카테고리입니다.");
        };
    }

    @Transactional(readOnly = true)
    public NewsListResponse getNewsList(
            String category,
            int page,
            int size
    ) {
        // 최신 뉴스부터 조회
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );

        Page<News> newsPage;

        if (category == null || category.isBlank()) {
            newsPage = newsRepository.findAll(pageable);
        } else {
            newsPage = newsRepository.findAllByCategory(category, pageable);
        }

        Page<NewsListItemResponse> responsePage =
                newsPage.map(NewsListItemResponse::from);

        return NewsListResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public NewsDetailResponse getNewsDetail(Long newsId) {

        // 뉴스 ID로 상세 정보 조회
        News news = newsRepository.findById(newsId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 뉴스입니다.")
                );

        return NewsDetailResponse.from(news);
    }

    @Transactional(readOnly = true)
    public NewsListResponse getRecommendedNews(
            Long userId,
            int page,
            int size
    ) {
        // 사용자가 선택한 관심사 조회
        List<UserInterest> userInterests =
                userInterestRepository.findAllByUserId(userId);

        List<String> categories = userInterests.stream()
                .map(userInterest -> userInterest.getInterest().getName())
                .toList();

        // 최신 뉴스부터 조회
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );

        Page<News> newsPage =
                newsRepository.findAllByCategoryIn(categories, pageable);

        Page<NewsListItemResponse> responsePage =
                newsPage.map(NewsListItemResponse::from);

        return NewsListResponse.from(responsePage);
    }
}