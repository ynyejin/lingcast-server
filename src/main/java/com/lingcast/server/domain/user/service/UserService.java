package com.lingcast.server.domain.user.service;

import com.lingcast.server.domain.user.dto.request.SignupRequest;
import com.lingcast.server.domain.user.dto.response.SignupResponse;
import com.lingcast.server.domain.user.entity.User;
import com.lingcast.server.domain.user.repository.UserRepository;
import com.lingcast.server.global.exception.BusinessException;
import com.lingcast.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 BCrypt 암호화
        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        // User 생성
        User user = User.create(
                request.getEmail(),
                encodedPassword,
                request.getNickname()
        );

        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }
}