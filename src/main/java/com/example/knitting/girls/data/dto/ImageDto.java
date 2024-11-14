package com.example.knitting.girls.data.dto;

import org.springframework.web.multipart.MultipartFile;

public class ImageDto { // "클라이언트와 상호작용"
    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
