package com.example.springAI.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springAI.service.EmailService;
import com.example.springAI.service.TextToImageService;
import com.example.springAI.service.TextToMusicService;
import com.example.springAI.service.TextToVideoService;
import com.example.springAI.service.TranscriptService;

@RestController
public class ChatController {

    @SuppressWarnings("unused")
    private final EmailService emailService;
    private final ChatClient chatClient;
    private TextToImageService textToImageService;
    private TextToVideoService textToVideoService;
    private TextToMusicService textToMusicService;
    @Autowired
    private TranscriptService service;

    // private final String INSTRUCTION = "You are a code generator who responds
    // only to code generation prompts and nothing else. Your sole purpose is code
    // generation. You must answer only in markdown code snippets. Use code comments
    // for explanations.";
    private final String INSTRUCTION = "You are a code generator. You must answer only in markdown code snippets. Use code comments for explanations.";
    private final String SUMMARIZE_INSTRUCTION = "Summarize the following transcript obtained from youtube video. Just provide the summary directly and nothing else";
    private final String EMAIL_INSTRUCTION = "You are an email body generator. Your output should be the main content of the email only. Do NOT include any email subject lines, or sender/receiver information. The output must be plain, context-specific email body text suitable for insertion into an email template. do not use square brackets";

    public ChatController(ChatClient chatClient, EmailService emailService,
            TextToImageService textToImageService, TextToVideoService textToVideoService,
            TextToMusicService textToMusicService) {
        // this.emailReaderService = emailReaderService;
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
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam("body") String body) {
        emailService.sendHtmlEmail(to, subject, username, password, body);
        return "Email sent successfully!";
    }

    @GetMapping("/summarize")
    public String getTranscript(@RequestParam String url) {
        String transcript = service.fetchTranscript(url);
        return chatClient.prompt().user(SUMMARIZE_INSTRUCTION + transcript).call().content();
    }

    @GetMapping("/generate-email-prompt")
    public String sendEmail(
            @RequestParam String prompt) {
        return chatClient.prompt().user(EMAIL_INSTRUCTION + prompt).call().content();
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

    @GetMapping("/generate-questions")
    public String generateQuestions(@RequestParam String topic, @RequestParam int no, @RequestParam String difficulty) {
        return chatClient.prompt().user("create " + no + " questions of " + topic
                + "and give me in an array in json with example format of :- [{question:'What is ...', options:[A,B,C,D], correct:0},...]. Options must be 4. Difficulty: "
                + difficulty + " . just return json. No other text except it").call().content();
    }

}
