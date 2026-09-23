package com.lingcast.server.domain.user.service;

import com.lingcast.server.domain.interest.entity.UserInterest;
import com.lingcast.server.domain.interest.repository.InterestRepository;
import com.lingcast.server.domain.interest.repository.UserInterestRepository;
import com.lingcast.server.domain.user.dto.request.SignupRequest;
import com.lingcast.server.domain.user.dto.request.UpdateUserRequest;
import com.lingcast.server.domain.user.dto.response.PreferenceResponse;
import com.lingcast.server.domain.user.dto.response.SignupResponse;
import com.lingcast.server.domain.user.dto.response.UpdateUserResponse;
import com.lingcast.server.domain.user.dto.response.UserResponse;
import com.lingcast.server.domain.user.entity.User;
import com.lingcast.server.domain.user.repository.UserRepository;
import com.lingcast.server.global.exception.BusinessException;
import com.lingcast.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lingcast.server.domain.user.dto.request.PreferenceRequest;
import com.lingcast.server.domain.interest.entity.Interest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterestRepository interestRepository;
    private final UserInterestRepository userInterestRepository;

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

    public UserResponse getMyInfo(Long userId) {

        // Access Token에서 추출한 사용자 ID로 회원 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    @Transactional
    public UpdateUserResponse updateMyInfo(
            Long userId,
            UpdateUserRequest request
    ) {

        // 현재 로그인한 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 변경
        user.updateNickname(request.nickname());

        return UpdateUserResponse.from(user);
    }

    @Transactional
    public PreferenceResponse savePreferences(
            Long userId,
            PreferenceRequest request
    ) {

        // 현재 로그인한 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 요청받은 관심 분야를 DB에서 조회
        List<Interest> interests =
                interestRepository.findAllByNameIn(request.categories());

        // 요청한 관심 분야 중 지원하지 않는 값이 있는지 확인
        if (interests.size() != request.categories().size()) {
            throw new BusinessException(ErrorCode.INVALID_PREFERENCE);
        }

        // 사용자의 영어 수준 설정
        user.updateEnglishLevel(request.englishLevel());

        // 선택한 관심 분야를 사용자와 연결하여 저장
        List<UserInterest> userInterests = interests.stream()
                .map(interest -> UserInterest.create(user, interest))
                .toList();

        userInterestRepository.saveAll(userInterests);

        return PreferenceResponse.of(
                user.getEnglishLevel(),
                request.categories()
        );
    }
}