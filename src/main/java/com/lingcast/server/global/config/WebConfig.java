package com.lingcast.server.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // /audio/podcasts/** 요청을 실제 음성 파일 저장 폴더와 연결
        String audioPath = Paths.get("uploads", "podcasts")
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        registry.addResourceHandler("/audio/podcasts/**")
                .addResourceLocations(audioPath);
    }
}