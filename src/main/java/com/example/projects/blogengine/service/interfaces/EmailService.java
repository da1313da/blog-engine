package com.example.projects.blogengine.service.interfaces;

public interface EmailService {
    void sendSimpleMessage(String to, String Subject, String text);
}
