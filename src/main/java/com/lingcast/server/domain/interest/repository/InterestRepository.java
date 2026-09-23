package com.lingcast.server.domain.interest.repository;

import com.lingcast.server.domain.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    // 요청받은 관심 분야 이름에 해당하는 Interest 조회
    List<Interest> findAllByNameIn(List<String> names);
}