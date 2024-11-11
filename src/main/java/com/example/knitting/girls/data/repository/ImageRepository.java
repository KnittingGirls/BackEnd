package com.example.knitting.girls.data.repository;

import com.example.knitting.girls.data.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}

// "DB와 데이터 통신"