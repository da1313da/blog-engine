package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.LoginRequest;
import com.example.projects.blogengine.api.response.LoginResponse;
import com.example.projects.blogengine.api.response.UserLoginResponse;
import com.example.projects.blogengine.exception.NotFoundException;
import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse response = new LoginResponse();
        Authentication auth;
        try{
            auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        }catch (Exception e){
            response.setResult(false);
            return response;
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        response.setResult(true);
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        User user = details.getUser();
        UserLoginResponse userLoginResponse = modelMapper.map(user, UserLoginResponse.class);
        userLoginResponse.setModeration(user.getIsModerator() == 1);
        userLoginResponse.setModerationCount(
                user.getIsModerator() == 1 ? postRepository.countByIsActiveAndModerationStatus((byte) 1, ModerationType.NEW) : 0);
        userLoginResponse.setSettings(user.getIsModerator() == 1);
        response.setUser(userLoginResponse);
        return response;
    }

    public LoginResponse logout() {
        LoginResponse response = new LoginResponse();
        try{
            SecurityContextHolder.clearContext();
        }catch (Exception e){
            return response;
        }
        response.setResult(true);
        return response;
    }

    public LoginResponse checkUserStatus(UserDetailsImpl userDetails) {
        LoginResponse response = new LoginResponse();
        if (userDetails == null){
            response.setResult(false);
            return response;
        }
        response.setResult(true);
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new NotFoundException(userDetails.getUser().getName() + " not found!", HttpStatus.BAD_REQUEST));
        UserLoginResponse userLoginResponse = modelMapper.map(user, UserLoginResponse.class);
        userLoginResponse.setModeration(user.getIsModerator() == 1);
        userLoginResponse.setModerationCount(
                user.getIsModerator() == 1? postRepository.countByIsActiveAndModerationStatus((byte) 1, ModerationType.NEW) : 0);
        userLoginResponse.setSettings(user.getIsModerator() == 1);
        response.setUser(userLoginResponse);
        return response;
    }
}
