package com.example.knitting.girls.data.dto;

import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.entity.PostImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private String content;
    private List<String> hashtags;

    @JsonProperty("images")
    private List<String> imageData;

    public PostDto(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.hashtags = post.getHashtags();
        this.imageData = post.getImages().stream()
                .map(pi -> "/uploads/" + pi.getImagePath())
                .collect(Collectors.toList());
    }
}

