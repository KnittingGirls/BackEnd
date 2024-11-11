package com.example.knitting.girls.data.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "kakao")
public class KakaoConfig {

    private String clientId;
    private String apiKey;
    private String redirectUri;

    // 생성자
    public KakaoConfig(String clientId, String apiKey, String redirectUri) {
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.redirectUri = redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void logProperties() {
        System.out.println("ClientId: " + clientId);
        System.out.println("ApiKey: " + apiKey);
        System.out.println("RedirectUri: " + redirectUri);
    }
}
