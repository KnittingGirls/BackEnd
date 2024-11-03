package com.example.knitting.girls.controller;

import com.example.knitting.girls.dto.ImageUploadReqDto;
import com.example.knitting.girls.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImageUploadController {

    @Autowired
    private ImageUploadService imageUploadService; // ImageUploadService 인터페이스 주입

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload"; // upload.html 파일을 반환
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("image") MultipartFile image, Model model) {
        ImageUploadReqDto requestDto = new ImageUploadReqDto(image); // DTO 생성
        String responseMessage = imageUploadService.uploadImage(requestDto); // DTO를 통해 이미지 업로드 서비스 호출
        model.addAttribute("message", responseMessage); // 결과 메시지를 모델에 추가
        return "upload"; // 업로드 결과를 보여줄 수 있는 뷰로 이동
    }
}
