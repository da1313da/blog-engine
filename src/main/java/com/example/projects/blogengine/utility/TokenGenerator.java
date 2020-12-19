package com.example.projects.blogengine.utility;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TokenGenerator {
    public synchronized String getToken(int size){
        StringBuffer buffer = new StringBuffer();
        String chars = "abcdefghiklmnopqrstvxyz0123456789";
        for (int i = 0; i < size; i++) {

            buffer.append(chars.charAt(new Random().nextInt(chars.length())));
        }
        return buffer.toString();
    }
}
