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
    private Long id;
    private String content;
    private List<String> hashtags;
    public PostDto(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.hashtags = post.getHashtags();
    }
}
