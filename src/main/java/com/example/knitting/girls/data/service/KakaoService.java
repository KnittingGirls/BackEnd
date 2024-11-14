package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.KakaoTokenResponseDto;
import com.example.knitting.girls.data.dto.KakaoUserInfoResponseDto;
import com.example.knitting.girls.data.configuration.KakaoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final KakaoConfig kakaoConfig;
    private final WebClient webClient;

    @Autowired
    public KakaoService(KakaoConfig kakaoConfig, WebClient.Builder webClientBuilder) {
        this.kakaoConfig = kakaoConfig;
        this.webClient = webClientBuilder.baseUrl("https://kauth.kakao.com").build();
    }

    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoConfig.getClientId())
                        .queryParam("code", code)
                        .queryParam("redirect_uri", kakaoConfig.getRedirectUri())
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                        Mono.error(new RuntimeException("API Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());


        // 액세스 토큰을 반환
        return kakaoTokenResponseDto != null ? kakaoTokenResponseDto.getAccessToken() : null;
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/user/me")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                        Mono.error(new RuntimeException("API Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }
}
