package com.example.knitting.girls.data.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private String content;
    private List<String> hashtags;
}