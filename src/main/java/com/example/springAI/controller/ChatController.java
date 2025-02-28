package com.example.springAI.controller;

import com.example.springAI.service.EmailReaderService;
import com.example.springAI.service.EmailService;
import com.example.springAI.service.TextToImageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @SuppressWarnings("unused")
    private final EmailReaderService emailReaderService;
    private final EmailService emailService;
    private final ChatClient chatClient;
    private TextToImageService textToImageService;

    public ChatController(ChatClient chatClient, EmailReaderService emailReaderService, EmailService emailService, TextToImageService textToImageService) {
        this.emailReaderService = emailReaderService;
        this.emailService = emailService;
        this.chatClient = chatClient;
        this.textToImageService = textToImageService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message) {
        return chatClient.prompt().user(message).call().content();
    }

        @PostMapping("/send")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam("message") String message) {
        emailService.sendHtmlEmail(to, subject, chatClient.prompt().user(message).call().content());
        return "Email sent successfully!";
    }

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateImage(@RequestParam String prompt) throws Exception {
        try {
            byte[] imageBytes = textToImageService.generateImage(prompt);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
        } catch (Exception e) {
            e.fillInStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
