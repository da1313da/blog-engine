package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.repository.PageRequestWithOffset;
import com.example.projects.blogengine.repository.PostsByConditionRepository;
import com.example.projects.blogengine.repository.view.PostsByConditionView;
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
        int count = 0;
        List<PostsByConditionView> posts = null;
        Map<String, Object> response = new HashMap<>();
        if (mode.equals("recent")){
            PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("timestamp").ascending());
            count = repository.getPostsCount();
            posts = repository.getPostsByConditions(page);
        } else if (mode.equals("popular")){
            PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("commentCount").descending());
            count = repository.getPostsCount();
            posts = repository.getPostsByConditions(page);
        } else if (mode.equals("best")){
            PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("likeCount").descending());
            count = repository.getPostsCount();
            posts = repository.getPostsByConditions(page);
        } else if (mode.equals("early")){
            PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("timestamp").descending());
            count = repository.getPostsCount();
            posts = repository.getPostsByConditions(page);
        }
        response.put("count", count);
        response.put("posts", posts);
        return response;
    }

//    @GetMapping("api/post/search")
//    public Object get
}
