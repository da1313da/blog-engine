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
    private ResponseService responseService;

    @GetMapping("api/post")
    public PostResponse getPostResponse(@RequestParam(name = "offset") int offset,
                                        @RequestParam(name = "limit") int limit,
                                        @RequestParam(name = "mode") String mode){
        return responseService.getPostResponse(limit, offset, mode);
    }

    @GetMapping("api/post/byDate")
    public PostResponse getPostResponseByDate(@RequestParam(name = "offset") int offset,
                                        @RequestParam(name = "limit") int limit,
                                        @RequestParam(name = "date") String date){
        return responseService.getPostResponseByDate(limit, offset, date);
    }

    @GetMapping("/api/post/byTag")
    public PostResponse getPostResponseByTag(@RequestParam(name = "offset") int offset,
                                              @RequestParam(name = "limit") int limit,
                                              @RequestParam(name = "tag") String tag){
        return responseService.getPostResponseByTag(limit, offset, tag);
    }

}
