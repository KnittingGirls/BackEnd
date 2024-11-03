package com.example.knitting.girls.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class ImageProcessReqDto {
    private MultipartFile image;

    public ImageProcessReqDto() {}

    public ImageProcessReqDto(MultipartFile image) {
        this.image = image;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}