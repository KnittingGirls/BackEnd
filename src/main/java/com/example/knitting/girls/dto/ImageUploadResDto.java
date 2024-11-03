package com.example.knitting.girls.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageUploadResDto {
    private String message;
    private String imageUrl;

    public ImageUploadResDto(String message, String imageUrl) {
        this.message = message;
        this.imageUrl = imageUrl;
    }
}
