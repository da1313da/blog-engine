package com.example.projects.blogengine.service.interfaces;

import com.example.projects.blogengine.api.request.EditProfileRequest;
import com.example.projects.blogengine.api.response.ErrorsResponse;
import com.example.projects.blogengine.security.UserDetailsImpl;

public interface EditProfileService {
    ErrorsResponse edit(EditProfileRequest request, UserDetailsImpl userDetails);
}
