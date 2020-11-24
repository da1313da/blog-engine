package com.example.projects.blogengine.service.interfaces;

import com.example.projects.blogengine.api.request.EditProfileRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.security.UserDetailsImpl;
import org.springframework.web.multipart.MultipartFile;

public interface EditProfileService {
    GenericResponse edit(MultipartFile file, EditProfileRequest request, UserDetailsImpl userDetails);
}
