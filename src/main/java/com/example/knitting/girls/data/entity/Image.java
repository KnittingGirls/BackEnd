package com.example.knitting.girls.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Lob
    @Column(name = "image_data", nullable = false, columnDefinition = "BLOB")
    private byte[] imageData;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadTime = LocalDateTime.now();
}
