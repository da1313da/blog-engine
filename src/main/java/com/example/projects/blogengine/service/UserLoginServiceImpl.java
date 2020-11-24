package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.LoginRequest;
import com.example.projects.blogengine.api.response.LoginResponse;
import com.example.projects.blogengine.api.response.UserLoginResponse;
import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.interfaces.UserLoginService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LoginResponse getLoginResponse(LoginRequest loginRequest) {
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
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();//todo some interface CustomUserDetails implement UserDetails and then impl
        User user = details.getUser();
        UserLoginResponse userLoginResponse = modelMapper.map(user, UserLoginResponse.class);
        userLoginResponse.setModeration(user.getIsModerator() == 1);
        userLoginResponse.setModerationCount(user.getIsModerator() == 1? postRepository.getModeratedPostCount(user, ModerationType.NEW) : 0);
        userLoginResponse.setSettings(user.getIsModerator() == 1);
        response.setUser(userLoginResponse);
        return response;
    }

    @Override
    public LoginResponse logout(UserDetailsImpl userDetails) {
        LoginResponse response = new LoginResponse();
        try{
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        }catch (Exception e){
            return response;
        }
        response.setResult(true);
        return response;
    }
}
