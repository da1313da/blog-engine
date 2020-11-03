package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.response.PostByIdResponse;
import com.example.projects.blogengine.api.response.PostResponse;
import com.example.projects.blogengine.service.ResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

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

    @GetMapping("/api/post/{id}")
    public ResponseEntity<PostByIdResponse> getPostByIdResponse(@PathVariable Integer id, HttpSession session){
        PostByIdResponse response = responseService.getPostByIdResponse(id, session);
        if (response == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

}
