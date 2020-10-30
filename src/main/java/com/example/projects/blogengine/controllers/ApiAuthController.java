package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.request.EmailData;
import com.example.projects.blogengine.api.request.LoginData;
import com.example.projects.blogengine.api.request.RegistrationData;
import com.example.projects.blogengine.api.response.BooleanResponse;
import com.example.projects.blogengine.api.response.CaptchaResponse;
import com.example.projects.blogengine.api.response.LoginResponse;
import com.example.projects.blogengine.api.response.RegistrationResponse;
import com.example.projects.blogengine.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class ApiAuthController {

    private final Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/api/auth/login", consumes = {"application/json"})
    public LoginResponse logIn(@RequestBody LoginData loginData, HttpSession session){
        return authService.getLoginResponse(loginData, session);
    }

    @GetMapping("/api/auth/check")
    public LoginResponse statusChek(HttpSession session){
        return authService.getUserStatus(session);
    }

    @PostMapping("/api/auth/register")
    public RegistrationResponse registration(@RequestBody RegistrationData registrationData){
        return authService.getRegistrationResponse(registrationData);
    }

    @GetMapping("/api/auth/captcha")
    public CaptchaResponse getCaptcha(){
        return authService.getCaptchaResponse();
    }

    @PostMapping("/api/auth/restore")
    public BooleanResponse restorePassword(@RequestBody EmailData email){
        return authService.getRestoreResult(email);
    }

}
