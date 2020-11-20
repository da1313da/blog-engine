package com.example.projects.blogengine.service.interfaces;

import com.example.projects.blogengine.security.UserDetailsImpl;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    Object upload(UserDetailsImpl user, MultipartFile file);
}
