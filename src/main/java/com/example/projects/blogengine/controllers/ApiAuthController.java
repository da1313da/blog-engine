package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.request.ChangePasswordRequest;
import com.example.projects.blogengine.api.request.EmailRequest;
import com.example.projects.blogengine.api.request.LoginRequest;
import com.example.projects.blogengine.api.request.RegistrationRequest;
import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.AuthService;
import com.example.projects.blogengine.service.interfaces.AuthCheckService;
import com.example.projects.blogengine.service.interfaces.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private AuthCheckService authCheckService;

    @PostMapping(value = "/api/auth/login", consumes = {"application/json"})
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(userLoginService.getLoginResponse(loginRequest));
    }

    @GetMapping("/api/auth/check")
    public LoginResponse statusChek(Principal principal){
        return authCheckService.getUserStatus(principal);
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<RegistrationResponse> registration(@RequestBody RegistrationRequest registrationData){
        RegistrationResponse response = authService.getRegistrationResponse(registrationData);
        if (response == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/auth/captcha")
    public CaptchaResponse getCaptcha(){
        return authService.getCaptchaResponse();
    }

    @PostMapping("/api/auth/restore")
    public BooleanResponse restorePassword(@RequestBody EmailRequest email){
        return authService.getRestoreResult(email);
    }

    @PostMapping("/api/auth/password")
    public ChangePasswordResponse changePassword(@RequestBody ChangePasswordRequest changePasswordData){
        return authService.getChangePasswordRequest(changePasswordData);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/auth/logout")
    public LoginResponse logout(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userLoginService.logout(userDetails);
    }
}
