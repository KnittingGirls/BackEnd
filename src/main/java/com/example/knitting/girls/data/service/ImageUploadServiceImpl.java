package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.ImageUploadReqDto;
import com.example.knitting.girls.data.entity.ImageEntity;
import com.example.knitting.girls.data.repository.ImageRepository;
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
    private RestTemplate restTemplate; // RestTemplate 주입

    private final String MODEL_SERVER_URL = "http://localhost:8081/process"; // 모델 서버 URL

    @Override
    public String uploadImage(ImageUploadReqDto requestDto) {
        MultipartFile image = requestDto.getImage(); // 요청 DTO에서 이미지 추출

        try {
            // 모델 서버로 이미지 전송
            return sendImageToModelServer(image);
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed: " + e.getMessage(); // 실패 메시지
        }
    }

    private String sendImageToModelServer(MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA); // 요청 헤더 설정

        // 이미지 데이터를 HTTP 엔티티로 래핑
        HttpEntity<MultipartFile> requestEntity = new HttpEntity<>(image, headers);

        // POST 요청을 통해 모델 서버에 전송
        return restTemplate.postForObject(MODEL_SERVER_URL, requestEntity, String.class);
    }
}
