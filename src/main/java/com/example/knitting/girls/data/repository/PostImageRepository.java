package com.example.knitting.girls.data.repository;

import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
}
