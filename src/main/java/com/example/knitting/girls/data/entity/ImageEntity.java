package com.example.knitting.girls.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter  // 모든 필드에 대한 getter 메서드 자동 생성
@Setter  // 모든 필드에 대한 setter 메서드 자동 생성
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_name")
    private String imageName;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;
}
