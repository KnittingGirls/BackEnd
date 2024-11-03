package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.ImageUploadReqDto;

public interface ImageUploadService {
    String uploadImage(ImageUploadReqDto requestDto);
}
