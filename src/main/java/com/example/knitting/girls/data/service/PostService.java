package com.example.knitting.girls.data.service;

import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.entity.User;
import com.example.knitting.girls.data.repository.PostRepository;
import com.example.knitting.girls.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Post> getAllPosts() { // 모든 게시글
        return postRepository.findAll();
    }

    public Post createPost(PostDto postDto, String nickname) { // 닉네임으로 유저 찾아서 글 작성
        User author = userRepository.findByNickname(nickname);
        if (author == null) {
            throw new IllegalArgumentException("User not found");
        }
        Post post = Post.builder()
                .content(postDto.getContent())
                .hashtags(postDto.getHashtags())
                .createdAt(LocalDateTime.now())
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
