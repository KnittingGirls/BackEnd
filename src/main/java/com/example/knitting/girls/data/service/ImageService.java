package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.ImageDto;
import com.example.knitting.girls.data.entity.ImageEntity;
import com.example.knitting.girls.data.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ImageService { // "실제 로직 처리"

    @Autowired
    private ImageRepository imageRepository;  // 이미지 리포지토리 사용

    // 이미지 업로드 처리 로직
    public String uploadImage(ImageDto imageDto) {
        try {
            // 이미지 DTO에서 MultipartFile을 가져옴
            MultipartFile imageFile = imageDto.getImage();

            // 이미지 데이터 바이트 배열로 변환
            byte[] imageData = imageFile.getBytes();

            // 이미지 엔티티 생성
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImageName(imageFile.getOriginalFilename());  // 파일 이름
            imageEntity.setImageData(imageData);  // 이미지 데이터

            // 이미지 저장 (DB에 저장)
            imageRepository.save(imageEntity);

            return "Image uploaded successfully!";
        } catch (IOException e) {
            return "Failed to upload image: " + e.getMessage();
        }
    }

    // 랜덤 이미지 조회 로직
    public byte[] getRandomImage() {
        // DB에서 모든 이미지 조회
        List<ImageEntity> images = imageRepository.findAll();

        if (images.isEmpty()) {
            return null;  // DB에 이미지가 없으면 null 반환
        }

        // 랜덤으로 이미지 선택
        Random random = new Random();
        ImageEntity randomImage = images.get(random.nextInt(images.size()));

        return randomImage.getImageData();  // 선택된 이미지의 데이터 반환
    }

    // 이미지를 이미지 ID로 조회하는 로직 (예: 이미지 이름 수정과 같은 기능을 추가할 때)
    public String processImage(Long imageId) {
        Optional<ImageEntity> optionalImage = imageRepository.findById(imageId);
        if (optionalImage.isPresent()) {
            ImageEntity imageEntity = optionalImage.get();
            String newName = "processed_" + imageEntity.getImageName();
            imageEntity.setImageName(newName);
            imageRepository.save(imageEntity);  // 변경된 이미지 엔티티 저장

            return "Image processed successfully with new name: " + newName;
        } else {
            return "Image not found!";
        }
    }
}
