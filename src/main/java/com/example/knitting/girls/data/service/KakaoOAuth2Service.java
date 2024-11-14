package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.KakaoUserDto;
import com.example.knitting.girls.data.entity.User;
import com.example.knitting.girls.data.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import java.util.List;

@Service
public class KakaoOAuth2Service {

    private final UserRepository userRepository;

    public KakaoOAuth2Service(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // OAuth2 인증 후, 카카오 사용자 정보 가져오기
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // AccessToken으로 카카오 사용자 정보 가져오기
        String accessToken = userRequest.getAccessToken().getTokenValue();
        KakaoUserDto kakaoUserDto = getUserInfo(accessToken);

        // 권한 설정
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        // 사용자 정보를 DB에 저장 또는 업데이트
        User user = saveOrUpdateUser(kakaoUserDto);

        return new DefaultOAuth2User(authorities, kakaoUserDto.toMap(), "id");
    }

    // 카카오 사용자 정보 받아오기
    public KakaoUserDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);

        JSONObject responseBody = new JSONObject(response.getBody());

        // 사용자 정보 파싱
        String nickname = responseBody.getJSONObject("properties").getString("nickname");
        String profileImageUrl = responseBody.getJSONObject("properties").getString("profile_image");

        // DTO로 변환
        KakaoUserDto kakaoUserDto = new KakaoUserDto();
        kakaoUserDto.setNickname(nickname);
        kakaoUserDto.setProfileImageUrl(profileImageUrl);

        return kakaoUserDto;
    }

    // 사용자 정보를 DB에 저장 또는 업데이트
    public User saveOrUpdateUser(KakaoUserDto kakaoUserDto) {
        User existingUser = userRepository.findByNickname(kakaoUserDto.getNickname());
        if (existingUser != null) {
            // 기존 사용자 -> 업데이트
            existingUser.setProfileImageUrl(kakaoUserDto.getProfileImageUrl());
            return userRepository.save(existingUser);
        } else {
            // 신규 사용자 -> 새로 저장
            User newUser = new User();
            newUser.setNickname(kakaoUserDto.getNickname());
            newUser.setProfileImageUrl(kakaoUserDto.getProfileImageUrl());
            return userRepository.save(newUser);
        }
    }
}
