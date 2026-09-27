package com.lingcast.server.domain.news.controller;

import com.lingcast.server.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}