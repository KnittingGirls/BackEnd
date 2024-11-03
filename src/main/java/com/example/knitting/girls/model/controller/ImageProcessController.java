package com.example.knitting.girls.model.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ImageProcessController {

    @PostMapping("/process-image")
    public ResponseEntity<String> processImage(@RequestParam("image") MultipartFile image) {
        // 이미지가 정상적으로 수신되었는지 확인
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("No image file received");
        }

        // 이미지 처리 로직을 여기에 추가합니다.
        try {
            // 예: 이미지 파일 이름 및 크기 출력
            String imageName = image.getOriginalFilename();
            long imageSize = image.getSize();

            // 추가적인 이미지 처리 로직 (리사이즈, 필터 적용 등)을 여기에 구현할 수 있습니다.
            // 현재는 단순히 수신된 이미지 정보를 로그로 출력합니다.
            System.out.println("Received image: " + imageName + " (Size: " + imageSize + " bytes)");

            // 처리 후 응답 메시지
            return ResponseEntity.ok("Image processed successfully: " + imageName);
        } catch (Exception e) {
            // 처리 중 오류 발생 시
            return ResponseEntity.status(500).body("Image processing failed: " + e.getMessage());
        }
    }
}
