package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.PostResponse;
import com.example.projects.blogengine.repository.PostsRepository;
import com.example.projects.blogengine.repository.PageRequestWithOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;

@Component
public class ResponseService {

    private final Logger logger = LoggerFactory.getLogger(ResponseService.class);

    @Autowired
    private PostsRepository repository;

    public PostResponse getPostResponse(int limit, int offset, String mode) {
        PostResponse response = new PostResponse();
        int count = repository.getPostsCount();
        response.setCount(count);
        if (count == 0){
            response.setPosts(new ArrayList<>());
            return response;
        }
        switch (mode) {
            case "recent":
                response.setPosts(repository.getPostsForPostResponse(ZonedDateTime.now(), new PageRequestWithOffset(limit, offset, Sort.by("time").descending())));
                break;
            case "popular":
                response.setPosts(repository.getPostsForPostResponse(ZonedDateTime.now(), new PageRequestWithOffset(limit, offset, Sort.by("comments").descending())));
                break;
            case "best":
                response.setPosts(repository.getPostsForPostResponse(ZonedDateTime.now(), new PageRequestWithOffset(limit, offset, Sort.by("likes").descending())));
                break;
            case "early":
                response.setPosts(repository.getPostsForPostResponse(ZonedDateTime.now(), new PageRequestWithOffset(limit, offset, Sort.by("time").ascending())));
                break;
        }
        return response;
    }
}
