package com.example.knitting.girls.service;

import com.example.knitting.girls.dto.ImageUploadReqDto;

public interface ImageUploadService {
    String uploadImage(ImageUploadReqDto requestDto);
}
