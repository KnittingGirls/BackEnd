package com.example.knitting.girls.model.service;

import com.example.knitting.girls.model.dto.ImageProcessReqDto;
import com.example.knitting.girls.model.dto.ImageProcessResDto;

public interface ImageProcessService {
    ImageProcessResDto processImage(ImageProcessReqDto requestDto);
}