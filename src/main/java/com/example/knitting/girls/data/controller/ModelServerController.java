package com.example.knitting.girls.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/model-server")
public class ModelServerController {

    private final RestTemplate restTemplate;

    @Autowired
    public ModelServerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/predict")
    public ResponseEntity<String> predictFromModel(@RequestParam("image") MultipartFile imageFile) throws IOException {
        File tempFile = File.createTempFile("upload-", imageFile.getOriginalFilename());
        imageFile.transferTo(tempFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String fastApiUrl = "http://localhost:8000/predict";
        ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String base64Image = (String) response.getBody().get("segmentation_image_base64");
            return ResponseEntity.ok(base64Image);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FastAPI 예측 실패");
    }
}

