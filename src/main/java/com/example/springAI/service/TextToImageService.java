package com.example.springAI.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class TextToImageService {

    // private static final String API_URL = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-3.5-large";
    private static final String API_URL = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-2";
    // private static final String API_KEY = "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"; //usage limit exceeded
    private static final String API_KEY = "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";

    public byte[] generateImage(String prompt) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");

        String requestBody = "{"
                + "\"inputs\": \"" + prompt + "\","
                + "\"options\": {\"use_cache\": false},"
                + "\"parameters\": {"
                + "\"guidance_scale\": 7.5,"
                + "\"num_inference_steps\": 50,"
                + "\"seed\": " + (int) (Math.random() * 100000) // 👈 Random seed
                + "}"
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // 🔹 Expecting byte[] instead of String to handle image response
        ResponseEntity<byte[]> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // Save image (optional)
            // Files.write(Paths.get("generated_image.png"), response.getBody());
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to generate image: " + response.getStatusCode());
        }
    }
}
