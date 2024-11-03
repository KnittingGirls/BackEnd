package com.example.knitting.girls.model.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/process-image")
                .allowedOrigins("http://localhost:8080") // 데이터 서버의 URL
                .allowedMethods("POST")
                .allowedHeaders("*");
    }
}