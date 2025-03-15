package com.example.knitting.girls.data.repository;

import com.example.knitting.girls.data.entity.Post;
import com.example.knitting.girls.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h = :tag")
    List<Post> findByHashtagsContaining(@Param("tag") String tag);
    List<Post> findByAuthor(User author);
}

