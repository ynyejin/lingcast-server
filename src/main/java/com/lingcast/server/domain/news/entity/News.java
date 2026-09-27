package com.lingcast.server.domain.news.entity;

import com.lingcast.server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(
            name = "original_url",
            nullable = false,
            unique = true,
            length = 1000
    )
    private String originalUrl;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    public static News create(
            String title,
            String summary,
            String source,
            String originalUrl,
            String imageUrl,
            String category,
            LocalDateTime publishedAt
    ) {
        News news = new News();
        news.title = title;
        news.summary = summary;
        news.source = source;
        news.originalUrl = originalUrl;
        news.imageUrl = imageUrl;
        news.category = category;
        news.publishedAt = publishedAt;
        return news;
    }
}