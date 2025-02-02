package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.entity.User;
import com.example.knitting.girls.data.repository.PostRepository;
import com.example.knitting.girls.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Post> getAllPosts() { // 모든 게시글
        return postRepository.findAll();
    }

    public Post createPost(PostDto postDto, String nickname, MultipartFile image) {
        User author = userRepository.findByNickname(nickname);
        if (author == null) {
            throw new IllegalArgumentException("User not found");
        }

        String imageData = null;
        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageBytes = image.getBytes();
                imageData = Base64.getEncoder().encodeToString(imageBytes); // java.util.Base64 사용
            } catch (IOException e) {
                throw new RuntimeException("파일 읽기 실패", e);
            }
        }

        Post post = Post.builder()
                .content(postDto.getContent())
                .hashtags(postDto.getHashtags())
                .createdAt(LocalDateTime.now())
                .imageData(imageData)
                .author(author)
                .build();
        return postRepository.save(post);
    }

    public List<Post> searchByTag(String tag) { // 해시태그 검색
        return postRepository.findByHashtagsContaining(tag);
    }

    public List<Post> getUserPosts(String nickname) { // 유저가 쓴 게시글 조회
        User author = userRepository.findByNickname(nickname);
        if (author == null) {
            throw new IllegalArgumentException("User not found");
        }
        return postRepository.findByAuthor(author);
    }
}

