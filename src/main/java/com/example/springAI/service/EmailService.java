package com.example.springAI.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Properties;

@Service
public class EmailService {

    private final JavaMailSenderImpl mailSender;

    public EmailService() {
        this.mailSender = new JavaMailSenderImpl();
    }

    public void sendHtmlEmail(String to, String subject, String username, String password, String htmlContent) {
        try {
            Properties config = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                config.load(input);
            } catch (Exception e) {
                throw new RuntimeException("Could not load mail config", e);
            }

            // Set up sender
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);
            mailSender.setUsername(username);
            mailSender.setPassword(password);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(mailSender.getUsername());

            mailSender.send(message);
            System.out.println("HTML email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
