package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.request.ChangePasswordRequest;
import com.example.projects.blogengine.api.request.EmailRequest;
import com.example.projects.blogengine.api.request.LoginRequest;
import com.example.projects.blogengine.api.request.RegistrationRequest;
import com.example.projects.blogengine.api.response.CaptchaResponse;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.api.response.LoginResponse;
import com.example.projects.blogengine.service.CaptchaService;
import com.example.projects.blogengine.service.LoginService;
import com.example.projects.blogengine.service.RegistrationUserService;
import com.example.projects.blogengine.service.UpdateUserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class ApiAuthController {

    private final Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    private final UpdateUserService updateUserService;
    private final RegistrationUserService registrationUserService;
    private final LoginService loginService;
    private final CaptchaService captchaService;


    @PostMapping(value = "/api/auth/login", consumes = {"application/json"})
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        return loginService.login(loginRequest);
    }

    @GetMapping("/api/auth/check")
    public LoginResponse statusChek(Principal principal){
        return loginService.checkUserStatus(principal);
    }

    @PostMapping("/api/auth/register")
    public GenericResponse registration(@RequestBody RegistrationRequest registrationData){
        return registrationUserService.registerNewUser(registrationData);
    }

    @GetMapping("/api/auth/captcha")
    public CaptchaResponse getCaptcha(){
        return captchaService.captcha();
    }

    @PostMapping("/api/auth/restore")
    public GenericResponse restorePassword(@RequestBody EmailRequest email){
        return updateUserService.restoreUserPassword(email);
    }

    @PostMapping("/api/auth/password")
    public GenericResponse changePassword(@RequestBody ChangePasswordRequest request){
        return updateUserService.changeUserPassword(request);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/auth/logout")
    public LoginResponse logout(){
        return loginService.logout();
    }

}
