package com.example.projects.blogengine.service.interfaces;

import com.example.projects.blogengine.api.request.EditProfileRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.security.UserDetailsImpl;

public interface EditProfileService {
    GenericResponse edit(EditProfileRequest request, UserDetailsImpl userDetails);
}
