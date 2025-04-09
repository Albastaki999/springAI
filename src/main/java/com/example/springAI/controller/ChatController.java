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

    // private final String INSTRUCTION = "You are a code generator who responds only to code generation prompts and nothing else. Your sole purpose is code generation. You must answer only in markdown code snippets. Use code comments for explanations.";
    private final String INSTRUCTION = "You are a code generator. You must answer only in markdown code snippets. Use code comments for explanations.";

    public ChatController(ChatClient chatClient, EmailReaderService emailReaderService, EmailService emailService,
            TextToImageService textToImageService) {
        this.emailReaderService = emailReaderService;
        this.emailService = emailService;
        this.chatClient = chatClient;
        this.textToImageService = textToImageService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message) {
        return chatClient.prompt().user(message).call().content();
    }

    @GetMapping("/code")
    public String generateCode(@RequestParam("message") String message) {
        return chatClient.prompt().user(INSTRUCTION + message).call().content();
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
