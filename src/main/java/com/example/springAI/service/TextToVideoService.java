package com.example.springAI.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TextToVideoService {
    private static final String API_URL = "https://api-inference.huggingface.co/models/TencentARC/AnimeGamer";
    private static final String API_KEY = "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";
    public byte[] generateVideo(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");

        String requestBody = """
        {
            "inputs": "%s"
        }
        """.formatted(prompt);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody(); // 🔁 Return video bytes (likely in .mp4 or .webm)
        } else {
            throw new RuntimeException("Failed to generate video: " + response.getStatusCode());
        }
    }
}
