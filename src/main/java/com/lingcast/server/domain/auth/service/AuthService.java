package com.lingcast.server.domain.auth.service;

import com.lingcast.server.domain.user.dto.request.LoginRequest;
import com.lingcast.server.domain.user.dto.response.LoginResponse;
import com.lingcast.server.domain.user.entity.User;
import com.lingcast.server.domain.user.repository.UserRepository;
import com.lingcast.server.global.exception.BusinessException;
import com.lingcast.server.global.exception.ErrorCode;
import com.lingcast.server.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lingcast.server.domain.user.dto.request.RefreshTokenRequest;
import com.lingcast.server.domain.user.dto.response.RefreshTokenResponse;
import com.lingcast.server.domain.auth.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {

        // 이메일로 사용자를 조회하고, 존재하지 않으면 로그인 실패 처리
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.INVALID_CREDENTIALS)
                );

        // 입력한 비밀번호와 DB에 저장된 BCrypt 비밀번호를 비교
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 인증에 사용할 Access Token과 재발급용 Refresh Token 생성
        String accessToken =
                jwtTokenProvider.createAccessToken(user.getId());

        String refreshToken =
                jwtTokenProvider.createRefreshToken(user.getId());

        // 발급한 Refresh Token을 Redis에 저장하고 30일 후 자동 만료
        refreshTokenRepository.save(
                user.getId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpirationSeconds()
        );

        return LoginResponse.of(
                accessToken,
                refreshToken,
                user,
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                jwtTokenProvider.getRefreshTokenExpirationSeconds()
        );
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {

        String refreshToken = request.refreshToken();

        // JWT의 서명과 만료시간이 유효한지 확인
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Access Token이 재발급에 사용되는 것을 방지
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Refresh Token에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // Redis에 저장된 해당 사용자의 Refresh Token 조회
        String savedRefreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN)
                );

        // 요청으로 받은 토큰과 Redis에 저장된 토큰이 같은지 확인
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰의 사용자가 실제로 존재하는지 확인
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 모든 검증을 통과하면 새로운 Access Token만 발급
        String newAccessToken =
                jwtTokenProvider.createAccessToken(userId);

        return RefreshTokenResponse.of(
                newAccessToken,
                jwtTokenProvider.getAccessTokenExpirationSeconds()
        );
    }
}