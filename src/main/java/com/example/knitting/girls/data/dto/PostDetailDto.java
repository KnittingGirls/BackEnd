package com.example.knitting.girls.data.dto;
import com.example.knitting.girls.data.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private List<String> hashtags;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImageUrl;
    private List<CommentDto> comments;
    private int likeCount;
    private List<UserDto> likedUsers;
    private int bookmarkCount;
    private List<UserDto> bookmarkedUsers;

    @JsonProperty("images")
    private List<String> imageData;

    public PostDetailDto(Post post,
                         List<CommentDto> comments,
                         List<UserDto> likedUsers,
                         List<UserDto> bookmarkedUsers) {
        this.id = post.getId();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.hashtags = post.getHashtags();
        this.authorId = post.getAuthor().getId();
        this.authorNickname = post.getAuthor().getNickname();
        this.authorProfileImageUrl = post.getAuthor().getProfileImageUrl();
        this.comments = comments != null ? comments : Collections.emptyList();
        this.likeCount = likedUsers != null ? likedUsers.size() : 0;
        this.likedUsers = likedUsers != null ? likedUsers : Collections.emptyList();
        this.bookmarkCount = bookmarkedUsers != null ? bookmarkedUsers.size() : 0;
        this.bookmarkedUsers = bookmarkedUsers != null ? bookmarkedUsers : Collections.emptyList();

        // 이미지 URL 매핑
        this.imageData = post.getImages().stream()
                .map(pi -> "/uploads/" + pi.getImagePath())
                .collect(Collectors.toList());
    }
}