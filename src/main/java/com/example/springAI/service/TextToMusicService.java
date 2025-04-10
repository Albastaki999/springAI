package com.example.springAI.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TextToMusicService {

    // private static final String API_URL = "https://api-inference.huggingface.co/models/facebook/musicgen-small";
    private static final String API_URL = "https://api-inference.huggingface.co/models/facebook/musicgen-medium";
    private static final String API_KEY = "Bearer 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"; // 🔒 Replace with your HF token

    public byte[] generateMusic(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"inputs\": \"%s\"}", prompt);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody(); // Will return .wav audio bytes
        } else {
            throw new RuntimeException("Failed to generate music: " + response.getStatusCode());
        }
    }
}
