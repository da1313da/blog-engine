package com.example.projects.blogengine.service.interfaces;

import com.example.projects.blogengine.api.response.LoginResponse;

import java.security.Principal;

public interface AuthCheckService {
    LoginResponse getUserStatus(Principal principal);
}
