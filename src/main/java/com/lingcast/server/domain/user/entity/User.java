package com.lingcast.server.domain.user.entity;

import com.lingcast.server.global.entity.BaseTimeEntity;
import com.lingcast.server.domain.common.EnglishLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "english_level", length = 20)
    private EnglishLevel englishLevel;

    public static User create(
            String email,
            String encodedPassword,
            String nickname
    ) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;

        return user;
    }

    // 사용자의 닉네임 변경
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // 사용자의 영어 학습 수준 변경
    public void updateEnglishLevel(EnglishLevel englishLevel) {
        this.englishLevel = englishLevel;
    }

}