package com.example.knitting.girls.data.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/images/*")
                .allowedOrigins("http://localhost:8080") // 프론트 서버의 URL
                .allowedMethods("POST", "GET")
                .allowedHeaders("*");
    }
}
 // CROS 설정