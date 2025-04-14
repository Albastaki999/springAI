package com.example.springAI.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class TextToImageService {

    private static final String API_URL = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-3.5-large";
    private static final String[] API_KEYS = {
        "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",
        "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",
        "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",
        "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" // muqsithali8@gmail.com
    };

    public byte[] generateImage(String prompt) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String requestBody = "{"
                + "\"inputs\": \"" + prompt + "\","
                + "\"options\": {\"use_cache\": false},"
                + "\"parameters\": {"
                + "\"guidance_scale\": 7.5,"
                + "\"num_inference_steps\": 50,"
                + "\"seed\": " + (int) (Math.random() * 100000)
                + "}"
                + "}";

        for (String apiKey : API_KEYS) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + apiKey);
                headers.set("Content-Type", "application/json");

                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<byte[]> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, byte[].class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody(); // return image if successful
                }
            } catch (Exception ex) {
                // Ignore and try next key
                System.out.println("API key failed: " + apiKey + " ➜ " + ex.getMessage());
            }
        }

        throw new RuntimeException("All API keys exhausted or failed.");
    }
}
