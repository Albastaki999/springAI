package com.example.springAI.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TranscriptService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchTranscript(String videoUrl) {
        String flaskUrl = "http://localhost:5000/get-transcript?url=" + videoUrl;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(flaskUrl, String.class);
            return response.getBody(); // JSON string with the transcript
        } catch (Exception e) {
            return "Error fetching transcript: " + e.getMessage();
        }
    }
}
