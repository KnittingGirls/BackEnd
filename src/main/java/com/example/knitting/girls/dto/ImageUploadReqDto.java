package com.example.knitting.girls.dto;

import org.springframework.web.multipart.MultipartFile;

public class ImageUploadReqDto {
    private MultipartFile image;

    // 생성자
    public ImageUploadReqDto(MultipartFile image) {
        this.image = image;
    }

    // Getter와 Setter
    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
