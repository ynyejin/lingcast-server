package com.lingcast.server.domain.interest.repository;

import com.lingcast.server.domain.interest.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    // 사용자에게 설정된 관심 분야 조회
    List<UserInterest> findAllByUserId(Long userId);
}