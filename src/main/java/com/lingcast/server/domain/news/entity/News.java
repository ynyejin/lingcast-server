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

    @Column(name = "original_url", nullable = false, length = 1000)
    private String originalUrl;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;
}