package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.configuration.KakaoConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/login")
public class KakaoLoginPageController {

    private final KakaoConfig kakaoConfig;

    public KakaoLoginPageController(KakaoConfig kakaoConfig) {
        this.kakaoConfig = kakaoConfig;
    }

    @GetMapping("/page")
    public String loginPage(Model model) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(kakaoConfig.getRedirectUri(), "UTF-8");
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + kakaoConfig.getClientId() + "&redirect_uri=" + redirectUri;
        model.addAttribute("location", location);

        return "login";
    }
}
