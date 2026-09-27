package com.lingcast.server.domain.news.controller;

import com.lingcast.server.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.lingcast.server.domain.news.dto.response.NewsListResponse;
import com.lingcast.server.global.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // 뉴스 수집 테스트용 API
    @PostMapping("/test/collect")
    public String collectNews(
            @RequestParam(defaultValue = "IT") String category
    ) {
        int savedCount = newsService.collectNews(category);

        return savedCount + "개의 뉴스를 저장했습니다.";
    }

    @GetMapping
    public ApiResponse<NewsListResponse> getNewsList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        NewsListResponse response =
                newsService.getNewsList(category, page, size);

        return ApiResponse.success(response, "뉴스 목록 조회에 성공했습니다.");
    }
}