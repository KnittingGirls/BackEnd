package com.example.knitting.girls.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class KakaoUserDto {
    private String nickname;
    private String profileImageUrl;

    public Map<String, Object> toMap() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("nickname", this.nickname);
        attributes.put("profile_image_url", this.profileImageUrl);
        return attributes;
    }
}