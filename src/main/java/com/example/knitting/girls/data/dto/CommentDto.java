package com.example.knitting.girls.data.dto;

import com.example.knitting.girls.data.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;  // ✅ 추가
    private UserDto author;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();  // ✅ 추가
        this.author = new UserDto(comment.getAuthor());
    }
}

