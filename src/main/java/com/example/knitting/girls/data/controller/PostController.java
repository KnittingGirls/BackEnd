package com.example.knitting.girls.data.controller;

import com.example.knitting.girls.data.dto.PostDto;
import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public Post createPost(@RequestBody PostDto postDto, @RequestParam String nickname) {
        return postService.createPost(postDto, nickname);
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

