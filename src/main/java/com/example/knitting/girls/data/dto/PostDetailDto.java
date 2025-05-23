package com.example.knitting.girls.data.dto;

import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.entity.PostImage;
import lombok.*;
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
    private List<String> hashtags;
    private List<String> imageData;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImageUrl;
    private List<CommentDto> comments;
    private int likeCount;
    private List<UserDto> likedUsers;
    private int bookmarkCount;
    private List<UserDto> bookmarkedUsers;

    public PostDetailDto(Post post, List<CommentDto> comments, List<UserDto> likedUsers, List<UserDto> bookmarkedUsers) {
        this.id = post.getId();
        this.content = post.getContent();
        this.hashtags = post.getHashtags();
        this.imageData = post.getImages().stream()
                .map(PostImage::getImageData)
                .collect(Collectors.toList());
        this.authorId = post.getAuthor().getId();
        this.authorNickname = post.getAuthor().getNickname();
        this.authorProfileImageUrl = post.getAuthor().getProfileImageUrl();
        this.comments = comments;
        this.likeCount = likedUsers.size();
        this.likedUsers = likedUsers;
        this.bookmarkCount = bookmarkedUsers.size();
        this.bookmarkedUsers = bookmarkedUsers;
    }
}
