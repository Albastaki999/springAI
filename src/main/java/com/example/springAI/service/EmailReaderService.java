package com.example.springAI.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailReaderService {

    private final String username;
    private final String password;

    public EmailReaderService(
            @Value("${mail.imap.username}") String username,
            @Value("${mail.imap.password}") String password) {
        this.username = username;
        this.password = password;
    }

    public void readEmails() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true"); // Secure IMAP connection
        properties.put("mail.imap.auth", "true");
        properties.put("mail.debug", "true"); // Enable debugging

        try {
            // Use Session with Properties
            Session session = Session.getInstance(properties);
            Store store = session.getStore("imap");
            store.connect(username, password);

            // Open Inbox
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Fetch Unread Emails
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            int i = 0;
            for (Message message : messages) {
                if (i == 10) {
                    break;
                }
                System.out.println("From: " + InternetAddress.toString(message.getFrom()));
                System.out.println("Subject: " + message.getSubject());

//                Object content = message.getContent();
//                if (content instanceof String) {
//                    System.out.println("Body: " + content);
//                } else {
//                    System.out.println("Body: [Non-text content]");
//                }

                // Mark as Read
                message.setFlag(Flags.Flag.SEEN, true);
                i++;
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
