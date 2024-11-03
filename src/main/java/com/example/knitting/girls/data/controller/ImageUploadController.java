package com.example.knitting.girls.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        String modelServerUrl = "http://localhost:8081/api/process-image"; // 모델 서버의 URL을 로컬로 수정

        // 이미지 데이터를 ByteArrayResource로 변환
        ByteArrayResource imageResource;
        try {
            imageResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename(); // 파일 이름 설정
                }
            };
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to read image file: " + e.getMessage());
        }

        // HTTP 요청 본문에 파일 데이터 설정
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", imageResource);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 모델 서버에 이미지 전송 요청
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(modelServerUrl, requestEntity, String.class);
            return ResponseEntity.ok("Image uploaded successfully: " + response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image to model server: " + e.getMessage());
        }
    }
}