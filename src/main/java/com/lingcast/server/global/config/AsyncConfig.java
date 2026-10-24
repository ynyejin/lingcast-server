
package com.lingcast.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "podcastAlignmentExecutor")
    public Executor podcastAlignmentExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        // M4 MacBook Air에서 우선 한 작업씩 처리
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);

        // 대기 가능한 자막 분석 작업
        executor.setQueueCapacity(20);

        executor.setThreadNamePrefix(
                "podcast-alignment-"
        );

        executor.initialize();

        return executor;
    }
}
