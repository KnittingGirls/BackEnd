package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.ImageUploadReqDto;
import com.example.knitting.girls.data.entity.ImageEntity;
import com.example.knitting.girls.data.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageTransService implements ImageUploadService {

    @Autowired
    private ImageRepository imageRepository; // 이미지 저장소 주입

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB 제한

    @Override
    public String uploadImage(ImageUploadReqDto requestDto) {
        MultipartFile image = requestDto.getImage(); // DTO에서 이미지 추출

        // 이미지 크기 체크
        if (image.getSize() > MAX_IMAGE_SIZE) {
            return "Image size exceeds the maximum limit of 10MB."; // 크기 초과 메시지
        }

        try {
            // 새로운 이미지 엔티티 생성
            ImageEntity newImage = new ImageEntity();
            newImage.setImageName(image.getOriginalFilename()); // 원본 파일 이름 저장
            newImage.setImageData(image.getBytes()); // 이미지 데이터 저장

            // 이미지 엔티티를 DB에 저장
            imageRepository.save(newImage);

            return "File uploaded successfully: " + newImage.getImageName(); // 성공
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed: " + e.getMessage(); // 실패
        }
    }
}
