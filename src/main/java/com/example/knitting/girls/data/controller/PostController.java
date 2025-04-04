package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.PostDetailDto;
import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.entity.Comment;
import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public Post createPost(@RequestParam("postDto") String postDtoStr,
                           @RequestParam String nickname,
                           @RequestPart(value = "images", required = false) List<MultipartFile> images) {  // 여러 이미지 업로드
        ObjectMapper objectMapper = new ObjectMapper();
        PostDto postDto;
        try {
            postDto = objectMapper.readValue(postDtoStr, PostDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("요청 형식 오류", e);
        }
        return postService.createPost(postDto, nickname, images);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public Post updatePost(@PathVariable Long postId, @RequestBody PostDto postDto, @RequestParam String nickname) {
        return postService.updatePost(postId, postDto, nickname);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, @RequestParam String nickname) {
        String message = postService.deletePost(postId, nickname);
        return ResponseEntity.ok(message);
    }

    // 모든 게시글 조회
    @GetMapping
    public List<PostDetailDto> getAllPosts() {
        return postService.getAllPosts();
    }

    // 특정 게시글 조회
    @GetMapping("/{postId}")
    public PostDetailDto getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    // 해시태그로 검색
    @GetMapping("/search")
    public List<PostDetailDto> searchByTag(@RequestParam String tag) {
        return postService.searchByTag(tag);
    }

    // 작성자로 검색
    @GetMapping("/user")
    public List<PostDetailDto> getUserPosts(@RequestParam String nickname) {
        return postService.getUserPosts(nickname);
    }

    // 댓글
    @PostMapping("/{postId}/comment")
    public PostDetailDto addComment(@PathVariable Long postId, @RequestParam String nickname, @RequestParam String content) {
        return postService.addComment(postId, nickname, content);
    }

    // 좋아요
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @RequestParam String nickname) {
        return ResponseEntity.ok(postService.likePost(postId, nickname));
    }

    // 북마크
    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<String> bookmarkPost(@PathVariable Long postId, @RequestParam String nickname) {
        return ResponseEntity.ok(postService.bookmarkPost(postId, nickname));
    }

    // 북마크 목록 조회
    @GetMapping("/bookmarks")
    public List<PostDetailDto> getBookmarkedPosts(@RequestParam String nickname) {
        return postService.getBookmarkedPosts(nickname);
    }
}




