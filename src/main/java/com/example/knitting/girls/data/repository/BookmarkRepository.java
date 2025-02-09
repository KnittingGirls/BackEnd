package com.example.knitting.girls.data.repository;

import com.example.knitting.girls.data.entity.Bookmark;
import com.example.knitting.girls.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
}

