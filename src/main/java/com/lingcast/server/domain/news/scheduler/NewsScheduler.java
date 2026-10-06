package com.lingcast.server.domain.news.scheduler;

import com.lingcast.server.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final NewsService newsService;

    // 매일 오전 6시에 IT 뉴스 수집
    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void collectITNews() {
        newsService.collectNews("IT");
    }

    // 매일 오전 6시 1분에 경제 뉴스 수집
    @Scheduled(cron = "0 1 6 * * *", zone = "Asia/Seoul")
    public void collectBusinessNews() {
        newsService.collectNews("경제");
    }

    // 매일 오전 6시 2분에 과학 뉴스 수집
    @Scheduled(cron = "0 2 6 * * *", zone = "Asia/Seoul")
    public void collectScienceNews() {
        newsService.collectNews("과학");
    }
}