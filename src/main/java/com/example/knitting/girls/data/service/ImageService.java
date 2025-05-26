package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.entity.Image;
import com.example.knitting.girls.data.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

// 안 씀

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    // 이미지 업로드
    public String uploadImage(MultipartFile imageFile) {
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                return "이미지를 선택하세요.";
            }

            // 이미지 데이터 바이트 배열로 변환
            byte[] imageData = imageFile.getBytes();

            // 이미지 엔티티 생성
            Image image = new Image();
            image.setImageName(imageFile.getOriginalFilename());
            image.setImageData(imageData);

            // 이미지 저장 (DB에 저장)
            imageRepository.save(image);

            return "Image uploaded successfully!";
        } catch (IOException e) {
            return "Failed to upload image: " + e.getMessage();
        }
    }

    // 랜덤 이미지 조회
    public byte[] getRandomImage() {
        // DB에서 모든 이미지 조회
        List<Image> images = imageRepository.findAll();

        if (images.isEmpty()) {
            return null; // DB에 이미지가 없으면 null 반환
        }

        // 랜덤 이미지 선택
        Random random = new Random();
        Image randomImage = images.get(random.nextInt(images.size()));

        return randomImage.getImageData();
    }

    // 이미지 ID로 조회 및 처리
    public String processImage(Long imageId) {
        Optional<Image> optionalImage = imageRepository.findById(imageId);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            String newName = "processed_" + image.getImageName();
            image.setImageName(newName);
            imageRepository.save(image);

            return "Image processed successfully with new name: " + newName;
        } else {
            return "Image not found!";
        }
    }
}
