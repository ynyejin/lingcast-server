package com.lingcast.server.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        // 환경변수의 Secret을 JWT 서명 및 검증에 사용할 키로 변환
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
        return createToken(
                userId,
                accessTokenExpiration,
                ACCESS_TOKEN_TYPE
        );
    }

    /**
     * Access Token 재발급에 사용할 Refresh Token 생성
     */
    public String createRefreshToken(Long userId) {
        return createToken(
                userId,
                refreshTokenExpiration,
                REFRESH_TOKEN_TYPE
        );
    }

    /**
     * 사용자 ID, 만료시간, 토큰 종류를 기반으로 JWT 생성
     */
    private String createToken(
            Long userId,
            long expiration,
            String tokenType
    ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                // JWT의 subject에 사용자 ID 저장
                .subject(String.valueOf(userId))

                // Access Token과 Refresh Token을 구분하기 위한 Claim
                .claim(TOKEN_TYPE_CLAIM, tokenType)

                // 토큰 발급 시간
                .issuedAt(now)

                // 토큰 만료 시간
                .expiration(expiryDate)

                // Secret Key로 JWT 서명
                .signWith(secretKey)

                .compact();
    }

    /**
     * JWT의 서명과 만료시간을 검증
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * JWT의 subject에서 사용자 ID 추출
     */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);

        return Long.valueOf(claims.getSubject());
    }

    /**
     * 전달받은 토큰이 Refresh Token인지 확인
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);

            String tokenType = claims.get(
                    TOKEN_TYPE_CLAIM,
                    String.class
            );

            return REFRESH_TOKEN_TYPE.equals(tokenType);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * JWT를 검증하고 내부 Claim 정보를 반환
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * API 응답에 사용할 Access Token 유효시간을 초 단위로 반환
     */
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }

    /**
     * API 응답에 사용할 Refresh Token 유효시간을 초 단위로 반환
     */
    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpiration / 1000;
    }

    /**
     * 전달받은 토큰이 Access Token인지 확인
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);

            String tokenType = claims.get(
                    TOKEN_TYPE_CLAIM,
                    String.class
            );

            return ACCESS_TOKEN_TYPE.equals(tokenType);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}