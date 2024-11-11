package com.example.knitting.girls.data.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public class KakaoConfig {

    private String clientId;  // 카카오 클라이언트 ID를 저장하는 필드
    private String apiKey;
    private String redirectUri;

    // 생성자
    public KakaoConfig(String clientId, String apiKey, String redirectUri) {
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.redirectUri = redirectUri;
    }

    // clientId를 반환하는 getter 메서드 추가
    public String getClientId() {
        return clientId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
