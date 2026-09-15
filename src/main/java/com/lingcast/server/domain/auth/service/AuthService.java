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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

        return LoginResponse.of(
                accessToken,
                refreshToken,
                user,
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                jwtTokenProvider.getRefreshTokenExpirationSeconds()
        );
    }
}