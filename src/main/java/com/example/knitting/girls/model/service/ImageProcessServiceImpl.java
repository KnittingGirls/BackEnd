package com.example.knitting.girls.model.service;

import com.example.knitting.girls.model.dto.ImageProcessReqDto;
import com.example.knitting.girls.model.dto.ImageProcessResDto;
import org.springframework.stereotype.Service;

@Service
public class ImageProcessServiceImpl implements ImageProcessService {

    @Override
    public ImageProcessResDto processImage(ImageProcessReqDto requestDto) {
        // 이미지 처리 로직 구현
        // 예: 머신러닝 모델에 이미지 전달, 처리 후 결과 반환 등

        String processedImageUrl = "http://example.com/processed-image.jpg"; // 처리된 이미지 URL
        return new ImageProcessResDto(processedImageUrl);
    }
}
