package com.lingcast.server.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        // 환경변수로 전달받은 Secret을 JWT 서명에 사용할 암호화 키로 변환
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * API 인증에 사용할 Access Token 생성
     */
    public String createAccessToken(Long userId) {
        return createToken(userId, accessTokenExpiration);
    }

    /**
     * Access Token 재발급에 사용할 Refresh Token 생성
     */
    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTokenExpiration);
    }

    /**
     * 사용자 ID와 만료시간을 기반으로 JWT 생성
     */
    private String createToken(Long userId, long expiration) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                // JWT의 subject에 사용자 ID 저장
                .subject(String.valueOf(userId))

                // 토큰 발급 시간
                .issuedAt(now)

                // 토큰 만료 시간
                .expiration(expiryDate)

                // Secret Key를 사용하여 토큰 서명
                .signWith(secretKey)

                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpiration / 1000;
    }
}