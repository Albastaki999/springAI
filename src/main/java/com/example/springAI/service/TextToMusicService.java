package com.example.springAI.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TextToMusicService {

    private static final String API_URL = "https://api-inference.huggingface.co/models/facebook/musicgen-small";
    private static final String[] API_KEYS = {
            "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",
            "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",
            "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"
    };

    public byte[] generateMusic(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String requestBody = String.format("{\"inputs\": \"%s\"}", prompt);

        for (String key : API_KEYS) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + key);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<byte[]> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, byte[].class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody(); // .wav audio bytes
                }
            } catch (Exception e) {
                System.out.println("Key failed: " + key + " ➜ " + e.getMessage());
            }
        }

        throw new RuntimeException("All API keys failed or exhausted.");
    }
}
