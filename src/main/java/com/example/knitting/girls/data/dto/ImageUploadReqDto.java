package com.example.knitting.girls.data.dto;

import org.springframework.web.multipart.MultipartFile;

public class ImageUploadReqDto {
    private MultipartFile image;

    public ImageUploadReqDto(MultipartFile image) {
        this.image = image;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
