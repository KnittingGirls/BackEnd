package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.KakaoUserInfoResponseDto;
import com.example.knitting.girls.data.entity.UserEntity;
import com.example.knitting.girls.data.repository.UserRepository;
import com.example.knitting.girls.data.security.JwtUtil;
import com.example.knitting.girls.data.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;  // JwtUtil 의존성 추가

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        // 카카오로부터 받은 code로 액세스 토큰을 받아옴
        String accessToken = kakaoService.getAccessTokenFromKakao(code); // 여기에서 액세스 토큰을 받아옴
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        // 카카오 사용자 정보 기반으로 로그인 또는 회원가입 처리
        UserEntity existingUserEntity = userRepository.findByKakaoId(userInfo.getId());

        if (existingUserEntity != null) {
            // 기존 사용자 로그인 처리
            String jwtToken = jwtUtil.generateToken(existingUserEntity.getAuthId());  // authId로 JWT 발급
            return ResponseEntity.ok("Bearer " + jwtToken);  // JWT 토큰 반환
        } else {
            // 새로운 사용자 회원가입 처리
            UserEntity newUserEntity = new UserEntity();
            newUserEntity.setKakaoId(userInfo.getId());
            newUserEntity.setNickname(userInfo.getKakaoAccount().getProfile().getNickName());
            newUserEntity.setEmail(userInfo.getKakaoAccount().getEmail());
            newUserEntity.setProfileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
            userRepository.save(newUserEntity);

            // 새로운 사용자 로그인 처리 (JWT 발급)
            String jwtToken = jwtUtil.generateToken(newUserEntity.getAuthId());  // authId로 JWT 발급
            return ResponseEntity.ok("Bearer " + jwtToken);  // JWT 토큰 반환
        }
    }
}