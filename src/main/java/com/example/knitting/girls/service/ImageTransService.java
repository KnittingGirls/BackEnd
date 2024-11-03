package com.example.knitting.girls.service;

import com.example.knitting.girls.dto.ImageUploadReqDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImageTransService implements ImageUploadService {

    @Override
    public String uploadImage(ImageUploadReqDto requestDto) {
        MultipartFile image = requestDto.getImage(); // DTO에서 이미지 파일 추출
        String filePath = "/path/to/upload/" + image.getOriginalFilename(); // 저장할 경로 설정

        try {
            image.transferTo(new File(filePath)); // 파일 저장
            return "File uploaded successfully: " + filePath; // 성공 메시지
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed: " + e.getMessage(); // 실패 메시지
        }
    }
}
