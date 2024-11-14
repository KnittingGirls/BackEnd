package com.example.knitting.girls.data.repository;

import com.example.knitting.girls.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByKakaoId(Long kakaoId);
}
