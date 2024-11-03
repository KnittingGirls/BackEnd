package com.example.knitting.girls.model.dto;

public class ImageProcessResDto {
    private String processedImageUrl;

    public ImageProcessResDto() {}

    public ImageProcessResDto(String processedImageUrl) {
        this.processedImageUrl = processedImageUrl;
    }

    public String getProcessedImageUrl() {
        return processedImageUrl;
    }

    public void setProcessedImageUrl(String processedImageUrl) {
        this.processedImageUrl = processedImageUrl;
    }
}
