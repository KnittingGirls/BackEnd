package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.entity.ImageEntity;
import com.example.knitting.girls.data.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ImageRepository imageRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        String modelServerUrl = "http://localhost:8081/api/process-image"; // 모델 서버 URL

        // 이미지 데이터를 ByteArrayResource로 변환
        ByteArrayResource imageResource;
        try {
            imageResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename(); // 파일 이름
                }
            };
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to read image file: " + e.getMessage());
        }

        // HTTP 요청 형식
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", imageResource);

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

    // 랜덤 이미지 출력 (아직 AI 구현 전이라)
    @GetMapping("/random")
    public ResponseEntity<ByteArrayResource> getRandomImage() {
        List<ImageEntity> images = imageRepository.findAll(); // DB 조회

        if (images.isEmpty()) {
            return ResponseEntity.notFound().build(); // null DB인 경우
        }

        Random random = new Random();
        ImageEntity randomImage = images.get(random.nextInt(images.size())); // 랜덤 이미지 선택

        ByteArrayResource resource = new ByteArrayResource(randomImage.getImageData()); // 이미지 데이터로 리소스 생성

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 이미지 타입
        headers.setContentDispositionFormData("attachment", randomImage.getImageName()); // 파일 이름

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
