package com.example.knitting.girls.data.service;


import com.example.knitting.girls.data.dto.ImageUploadReqDto;
import com.example.knitting.girls.data.entity.ImageEntity;
import com.example.knitting.girls.data.repository.ImageRepository;
import com.example.knitting.girls.data.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    @Autowired
    private ImageRepository imageRepository; // 이미지 저장소 주입

    private final String MODEL_SERVER_URL = "http://localhost:8081/process"; // 모델 서버 URL

    @Override
    public String uploadImage(ImageUploadReqDto requestDto) {
        MultipartFile image = requestDto.getImage(); // 요청 DTO에서 이미지 추출

        try {
            // 이미지 엔티티 생성 및 속성 설정
            ImageEntity newImage = new ImageEntity();
            newImage.setImageName(image.getOriginalFilename()); // 이미지 이름 설정
            newImage.setImageData(image.getBytes()); // 이미지 데이터 설정

            // 이미지 엔티티를 데이터베이스에 저장
            imageRepository.save(newImage);

            // 모델 서버로 이미지 전송
            sendImageToModelServer(newImage);

            return "File uploaded successfully: " + newImage.getImageName(); // 성공 메시지
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed: " + e.getMessage(); // 실패 메시지
        }
    }

    private void sendImageToModelServer(ImageEntity imageEntity) {
        RestTemplate restTemplate = new RestTemplate(); // REST 요청을 위한 RestTemplate 객체 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA); // 요청 헤더 설정

        // 이미지 엔티티를 HTTP 엔티티로 래핑
        HttpEntity<ImageEntity> requestEntity = new HttpEntity<>(imageEntity, headers);

        // POST 요청을 통해 모델 서버에 전송
        restTemplate.postForEntity(MODEL_SERVER_URL, requestEntity, String.class);
    }
}