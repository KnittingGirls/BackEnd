package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.ImageDto;
import com.example.knitting.girls.data.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/images")
public class ImageController { // "클라이언트 요청을 받아서 처리"

    @Autowired
    private ImageService imageService;

    // 이미지 업로드 엔드포인트
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@ModelAttribute ImageDto imageDto) {
        String result = imageService.uploadImage(imageDto);
        return ResponseEntity.ok(result);
    }

    // 랜덤 이미지 조회 엔드포인트
    @GetMapping("/random")
    public ResponseEntity<byte[]> getRandomImage() {
        byte[] imageData = imageService.getRandomImage();

        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }
}
