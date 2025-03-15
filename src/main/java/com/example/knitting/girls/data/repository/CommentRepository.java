package com.example.knitting.girls.data.repository;

import com.example.knitting.girls.data.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {}

