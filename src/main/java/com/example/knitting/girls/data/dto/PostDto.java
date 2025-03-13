package com.example.knitting.girls.data.dto;

import com.example.knitting.girls.data.entity.Post;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private String content;
    private List<String> hashtags;
    public PostDto(Post post) {
        this.content = post.getContent();
        this.hashtags = post.getHashtags();
    }
}
