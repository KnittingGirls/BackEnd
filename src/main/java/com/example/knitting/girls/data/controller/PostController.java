package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.entity.Comment;
import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping(consumes = {"multipart/form-data"})
    public Post createPost(@RequestParam("postDto") String postDtoStr, @RequestParam String nickname, @RequestPart(value = "image", required = false) MultipartFile image) {
        ObjectMapper objectMapper = new ObjectMapper();
        PostDto postDto;
        try {
            postDto = objectMapper.readValue(postDtoStr, PostDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format for postDto", e);
        }
        return postService.createPost(postDto, nickname, image);
    }

    // 모든 게시글 조회
    @GetMapping
    public List<PostDto> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        List<PostDto> postDtos = posts.stream()
                .map(post -> new PostDto(post))
                .collect(Collectors.toList());
        return postDtos;
    }


    // 해시태그로 검색
    @GetMapping("/search")
    public List<Post> searchByTag(@RequestParam String tag) {
        return postService.searchByTag(tag);
    }

    // 작성자로 검색
    @GetMapping("/user")
    public List<Post> getUserPosts(@RequestParam String nickname) {
        return postService.getUserPosts(nickname);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public Post updatePost(@PathVariable Long postId, @RequestBody PostDto postDto, @RequestParam String nickname) {
        return postService.updatePost(postId, postDto, nickname);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId, @RequestParam String nickname) {
        postService.deletePost(postId, nickname);
    }

    // 좋아요
    @PostMapping("/{postId}/like")
    public void likePost(@PathVariable Long postId, @RequestParam String nickname) {
        postService.likePost(postId, nickname);
    }

    // 댓글
    @PostMapping("/{postId}/comment")
    public Comment addComment(@PathVariable Long postId, @RequestParam String nickname, @RequestParam String content) {
        return postService.addComment(postId, nickname, content);
    }

    // 북마크
    @PostMapping("/{postId}/bookmark")
    public void bookmarkPost(@PathVariable Long postId, @RequestParam String nickname) {
        postService.bookmarkPost(postId, nickname);
    }

    // 내가 스크랩한 게시글 조회
    @GetMapping("/bookmarks")
    public List<Post> getBookmarkedPosts(@RequestParam String nickname) {
        return postService.getBookmarkedPosts(nickname);
    }
}


