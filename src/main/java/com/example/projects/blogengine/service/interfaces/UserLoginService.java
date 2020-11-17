package com.example.projects.blogengine.service.interfaces;

import com.example.projects.blogengine.api.request.LoginRequest;
import com.example.projects.blogengine.api.response.LoginResponse;

public interface UserLoginService {
    LoginResponse getLoginResponse(LoginRequest loginRequest);
}
