package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public Post createPost(
            @RequestParam("postDto") String postDtoStr,
            @RequestParam String nickname,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        ObjectMapper objectMapper = new ObjectMapper();
        PostDto postDto;
        try {
            postDto = objectMapper.readValue(postDtoStr, PostDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format for postDto", e);
        }

        return postService.createPost(postDto, nickname, image);
    }


    @GetMapping("/search")
    public List<Post> searchByTag(@RequestParam String tag) {
        return postService.searchByTag(tag);
    }

    @GetMapping("/user")
    public List<Post> getUserPosts(@RequestParam String nickname) {
        return postService.getUserPosts(nickname);
    }
}

