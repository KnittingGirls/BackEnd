package com.example.knitting.girls.data.repository;

import com.example.knitting.girls.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByKakaoId(Long kakaoId);
}
