package com.example.projects.blogengine.service.interfaces;

import com.example.projects.blogengine.api.request.LoginRequest;
import com.example.projects.blogengine.api.response.LoginResponse;
import com.example.projects.blogengine.security.UserDetailsImpl;

public interface UserLoginService {
    LoginResponse getLoginResponse(LoginRequest loginRequest);
    LoginResponse logout(UserDetailsImpl userDetails);
}
