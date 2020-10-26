package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.response.PostResponse;
import com.example.projects.blogengine.service.ResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {
    private Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    @Autowired
    private ResponseService responseBuilder;

    @GetMapping("api/post")
    public PostResponse getPostResponse(@RequestParam(name = "offset") int offset,
                                        @RequestParam(name = "limit") int limit,
                                        @RequestParam(name = "mode") String mode){
        return responseBuilder.getPostResponse(limit, offset, mode);
    }


}
