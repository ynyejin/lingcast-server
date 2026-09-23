package com.lingcast.server.domain.interest.repository;

import com.lingcast.server.domain.interest.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
}