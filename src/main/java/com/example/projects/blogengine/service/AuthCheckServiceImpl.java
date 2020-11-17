package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.LoginResponse;
import com.example.projects.blogengine.api.response.UserLoginResponse;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.service.interfaces.AuthCheckService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AuthCheckServiceImpl implements AuthCheckService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LoginResponse getUserStatus(Principal principal) {
        LoginResponse response = new LoginResponse();
        if (principal == null){
            response.setResult(false);
            return response;
        }
        response.setResult(true);
        User user = userRepository.getUserByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(principal.getName() + " not found"));
        UserLoginResponse userLoginResponse = modelMapper.map(user, UserLoginResponse.class);
        userLoginResponse.setModeration(user.getIsModerator() == 1);
        userLoginResponse.setModerationCount(user.getIsModerator() == 1? postRepository.getPostCountModeratedByUser(user) : 0);
        userLoginResponse.setSettings(user.getIsModerator() == 1);
        response.setUser(userLoginResponse);
        return response;
    }
}
