package com.example.projects.blogengine.security;

import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository
                .getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
        return new UserDetailsImpl(user);
    }
}
