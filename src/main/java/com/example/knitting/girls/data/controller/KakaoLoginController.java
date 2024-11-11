package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.KakaoUserInfoResponseDto;
import com.example.knitting.girls.data.entity.User;
import com.example.knitting.girls.data.repository.UserRepository;
import com.example.knitting.girls.data.security.JwtUtil;
import com.example.knitting.girls.data.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
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
        User existingUser = userRepository.findByKakaoId(userInfo.getId());

        if (existingUser != null) {
            // 기존 사용자 로그인 처리
            String jwtToken = jwtUtil.generateToken(existingUser.getAuthId());  // authId로 JWT 발급
            return ResponseEntity.ok("Bearer " + jwtToken);  // JWT 토큰 반환
        } else {
            // 새로운 사용자 회원가입 처리
            User newUser = new User();
            newUser.setKakaoId(userInfo.getId());
            newUser.setNickname(userInfo.getKakaoAccount().getProfile().getNickName());
            newUser.setEmail(userInfo.getKakaoAccount().getEmail());
            newUser.setProfileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
            userRepository.save(newUser);

            // 새로운 사용자 로그인 처리 (JWT 발급)
            String jwtToken = jwtUtil.generateToken(newUser.getAuthId());  // authId로 JWT 발급
            return ResponseEntity.ok("Bearer " + jwtToken);  // JWT 토큰 반환
        }
    }
}