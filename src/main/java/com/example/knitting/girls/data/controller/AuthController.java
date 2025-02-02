package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.KakaoUserDto;
import com.example.knitting.girls.data.entity.User;
import com.example.knitting.girls.data.security.JwtTokenProvider;
import com.example.knitting.girls.data.service.KakaoOAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private KakaoOAuth2Service kakaoOAuth2Service;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 로그인 페이지 매핑 (GET /auth/login)
    @GetMapping("/login")
    public String login() {
        return "login";  // login.html로 리디렉션 (로그인 페이지)
    }

    // 카카오 로그인 후 콜백을 처리하는 메서드
    @GetMapping("/login/callback")
    public String kakaoLogin(@RequestParam String code, Model model, HttpServletResponse response) {
        // 카카오 액세스 토큰 가져오기
        String accessToken = getKakaoAccessToken(code);

        // 카카오 사용자 정보 가져오기
        KakaoUserDto userInfo = kakaoOAuth2Service.getUserInfo(accessToken);

        // 사용자 저장 또는 업데이트
        User user = kakaoOAuth2Service.saveOrUpdateUser(userInfo);

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getId().toString());

        // 토큰을 쿠키에 저장
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");  // 전체 경로에서 접근 가능
        cookie.setMaxAge(60 * 60 * 24); // 1일 동안
        cookie.setHttpOnly(true);
        cookie.setSecure(false);  // 개발 환경에서는 false로 설정 (HTTPS가 아닌 경우)
        response.addCookie(cookie);

        model.addAttribute("userInfo", userInfo);

        return "login_success";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return "login"; // 로그인 페이지로 리디렉션
    }

    // 카카오 액세스 토큰 받아오기
    private String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String body = "grant_type=authorization_code"
                + "&client_id=23d1da761622fc79819d5e2b74ccf70a"
                + "&redirect_uri=http://localhost:8080/auth/login/callback"
                + "&code=" + code;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        JSONObject responseBody = new JSONObject(response.getBody());
        return responseBody.getString("access_token");
    }
}
