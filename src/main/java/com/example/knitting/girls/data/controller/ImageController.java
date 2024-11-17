package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.ImageDto;
import com.example.knitting.girls.data.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    // 이미지 업로드 엔드포인트
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                return ResponseEntity.badRequest().body("이미지를 선택하세요.");
            }

            String result = imageService.uploadImage(image); // 이미지 저장 서비스 호출
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패: " + e.getMessage());
        }
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
