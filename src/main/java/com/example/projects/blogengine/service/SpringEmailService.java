package com.example.projects.blogengine.service;

import com.example.projects.blogengine.config.BlogProperties;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpringEmailService{

    private final JavaMailSender mailSender;
    private final BlogProperties blogProperties;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(blogProperties.getEmailAddress());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
