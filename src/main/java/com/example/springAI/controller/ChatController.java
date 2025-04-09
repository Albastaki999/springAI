package com.example.springAI.controller;

import com.example.springAI.service.EmailReaderService;
import com.example.springAI.service.EmailService;
import com.example.springAI.service.TextToImageService;
import com.example.springAI.service.TextToMusicService;
import com.example.springAI.service.TextToVideoService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpHeaders;
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
    private TextToVideoService textToVideoService;
    private TextToMusicService textToMusicService;

    // private final String INSTRUCTION = "You are a code generator who responds
    // only to code generation prompts and nothing else. Your sole purpose is code
    // generation. You must answer only in markdown code snippets. Use code comments
    // for explanations.";
    private final String INSTRUCTION = "You are a code generator. You must answer only in markdown code snippets. Use code comments for explanations.";

    public ChatController(ChatClient chatClient, EmailReaderService emailReaderService, EmailService emailService,
            TextToImageService textToImageService, TextToVideoService textToVideoService, TextToMusicService textToMusicService) {
        this.emailReaderService = emailReaderService;
        this.emailService = emailService;
        this.chatClient = chatClient;
        this.textToImageService = textToImageService;
        this.textToVideoService = textToVideoService;
        this.textToMusicService = textToMusicService;
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

    @GetMapping(value = "/generate-video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> generateVideo(@RequestParam String prompt) {
        try {
            byte[] videoBytes = textToVideoService.generateVideo(prompt);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=generated_video.mp4")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(videoBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping(value = "/generate-music", produces = "audio/wav")
    public ResponseEntity<byte[]> generateMusic(@RequestParam String prompt) {
        try {
            byte[] audioBytes = textToMusicService.generateMusic(prompt);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated_music.wav")
                    .body(audioBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
