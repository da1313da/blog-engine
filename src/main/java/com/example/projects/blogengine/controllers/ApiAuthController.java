package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.request.ChangePasswordRequest;
import com.example.projects.blogengine.api.request.EmailRequest;
import com.example.projects.blogengine.api.request.LoginRequest;
import com.example.projects.blogengine.api.request.RegistrationRequest;
import com.example.projects.blogengine.api.response.CaptchaResponse;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.api.response.LoginResponse;
import com.example.projects.blogengine.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ApiAuthController {

    private final Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/api/auth/login", consumes = {"application/json"})
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @GetMapping("/api/auth/check")
    public LoginResponse statusChek(Principal principal){
        return authService.checkUserStatus(principal);
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<GenericResponse> registration(@RequestBody RegistrationRequest registrationData){
        GenericResponse response = authService.registration(registrationData);
        if (response == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/auth/captcha")
    public CaptchaResponse getCaptcha(){
        return authService.captcha();
    }

    @PostMapping("/api/auth/restore")
    public GenericResponse restorePassword(@RequestBody EmailRequest email){
        return authService.restorePassword(email);
    }

    @PostMapping("/api/auth/password")
    public GenericResponse changePassword(@RequestBody ChangePasswordRequest request){
        return authService.changePassword(request);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/auth/logout")
    public LoginResponse logout(){
        return authService.logout();
    }
}
