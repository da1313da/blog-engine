package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.ModerationRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.api.response.PostResponse;
import com.example.projects.blogengine.exception.AccessDeniedException;
import com.example.projects.blogengine.exception.NotFoundException;
import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class PostResponseService {

    private final Logger logger = LoggerFactory.getLogger(PostResponseService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public PostResponse getPostById(int id, UserDetailsImpl user) {
        Post post = postRepository.getPostById(id)
                .orElseThrow(() -> new NotFoundException("Post with id " + id + " not found!", HttpStatus.NOT_FOUND));
        if (user != null && user.getUser().getIsModerator() != 1 && !post.getUser().getId().equals(user.getUser().getId())){
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
        return modelMapper.map(post, PostResponse.class);
    }

    public GenericResponse moderatePost(ModerationRequest request, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("Post with id " + request.getPostId() + " not found!", HttpStatus.BAD_REQUEST));
        User moderator = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User with id " +userDetails.getUser().getId() + " not found!", HttpStatus.BAD_REQUEST));
        if (moderator.getIsModerator() != 1) {
            throw new AccessDeniedException("User with id doesn't have permission to moderate!", HttpStatus.BAD_REQUEST);
        }
        GenericResponse response = new GenericResponse();
        post.setModerationStatus(request.getDecision().equals("accept") ? ModerationType.ACCEPTED : ModerationType.DECLINED);
        postRepository.save(post);
        response.setResult(true);
        return response;
    }

}
