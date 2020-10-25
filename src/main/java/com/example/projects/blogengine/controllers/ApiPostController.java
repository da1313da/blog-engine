package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.data.PageRequestWithOffset;
import com.example.projects.blogengine.data.PostsByConditionRepository;
import com.example.projects.blogengine.data.PostsByConditionView;
import com.example.projects.blogengine.data.PostsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiPostController {
    private Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    @Autowired
    private PostsDao service;

    @Autowired
    private PostsByConditionRepository repository;

    @GetMapping("api/post")
    public Map<String, Object> getAllPosts(@RequestParam(name = "offset") long offset,
                                           @RequestParam(name = "limit") int limit,
                                           @RequestParam(name = "mode") String mode){
        //check frontend params?
        if (mode.equals("recent")){
            PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("timestamp").ascending());
            int count = repository.getPostsCount();
            List<PostsByConditionView> posts = repository.getPostsByConditions(page);
            return configureResponse(count, posts);
        } else if (mode.equals("popular")){
            PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("commentCount").descending());
            int count = repository.getPostsCount();
            List<PostsByConditionView> posts = repository.getPostsByConditions(page);
            return configureResponse(count, posts);
        } else if (mode.equals("best")){
            PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("likeCount").descending());
            int count = repository.getPostsCount();
            List<PostsByConditionView> posts = repository.getPostsByConditions(page);
            return configureResponse(count, posts);
        } else if (mode.equals("early")){
            PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("timestamp").descending());
            int count = repository.getPostsCount();
            List<PostsByConditionView> posts = repository.getPostsByConditions(page);
            return configureResponse(count, posts);
        }
        return null;
    }

    private Map<String, Object> configureResponse(int count, List<PostsByConditionView> posts){
        if (posts.size() == 0){
            return null;
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("posts", posts);
            return response;
        }
    }
}
