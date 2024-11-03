package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.ImageUploadReqDto;
import com.example.knitting.girls.data.service.ImageUploadService;
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
    private ImageUploadService imageUploadService;

    @GetMapping("/upload")
    public String showUploadForm() {

        return "upload"; // upload.html 파일을 반환
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("image") MultipartFile image, Model model) {
        ImageUploadReqDto requestDto = new ImageUploadReqDto(image);
        String responseMessage = imageUploadService.uploadImage(requestDto);
        model.addAttribute("message", responseMessage);
        return "upload";
    }
}
