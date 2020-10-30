package com.example.projects.blogengine.service;

public interface EmailService {
    void sendSimpleMessage(String to, String Subject, String text);
}
