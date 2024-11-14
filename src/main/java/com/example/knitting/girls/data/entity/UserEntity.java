package com.example.knitting.girls.data.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동으로 생성되는 ID (식별자)
    private Long authId;  // 사용자 식별을 위한 auth_id

    private Long kakaoId;  // 카카오 ID (기존 id 필드, 카카오에서 제공하는 고유 ID)
    private String nickname;
    private String email;
    private String profileImageUrl;

    // 추가적인 사용자 정보를 저장할 수 있음 (예: 나이, 성별 등)
}