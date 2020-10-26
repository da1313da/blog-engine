package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.PostResponse;
import com.example.projects.blogengine.repository.PostsRepository;
import com.example.projects.blogengine.repository.PageRequestWithOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Component
public class ResponseService {

    private final Logger logger = LoggerFactory.getLogger(ResponseService.class);

    @Autowired
    private PostsRepository repository;

    public PostResponse getPostResponse(int limit, int offset, String mode) {
        PostResponse response = new PostResponse();
        int count = repository.getPostsCount(ZonedDateTime.now());
        response.setCount(count);
        if (count == 0){
            response.setPosts(new ArrayList<>());
            return response;
        }
        switch (mode) {
            case "recent":
                PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("time").descending());
                response.setPosts(repository.getPostsForPostResponse(ZonedDateTime.now(), page));
                break;
            case "popular":
                page = new PageRequestWithOffset(limit, offset, Sort.by("comments").descending());
                response.setPosts(repository.getPostsForPostResponse(ZonedDateTime.now(), page));
                break;
            case "best":
                page = new PageRequestWithOffset(limit, offset, Sort.by("likes").descending());
                response.setPosts(repository.getPostsForPostResponse(ZonedDateTime.now(), page));
                break;
            case "early":
                page = new PageRequestWithOffset(limit, offset, Sort.by("time").ascending());
                response.setPosts(repository.getPostsForPostResponse(ZonedDateTime.now(), page));
                break;
        }
        return response;
    }

    public PostResponse getPostResponseByDate(int limit, int offset, String date){
        PostResponse postResponse = new PostResponse();
        String startDateStr = date + " 00:00:00";
        String endDateStr = date + " 23:59:59";
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("Europe/Moscow"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("Europe/Moscow"));
        int count = repository.getPostsCountByDate(ZonedDateTime.now(), start, end);
        postResponse.setCount(count);
        if (count == 0){
            postResponse.setPosts(new ArrayList<>());
            return postResponse;
        }
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        postResponse.setPosts(repository.getPostsForPostResponseByDate(ZonedDateTime.now(), start, end, page));
        return postResponse;
    }

    public PostResponse getPostResponseByTag(int limit, int offset, String tag) {
        PostResponse postResponse = new PostResponse();
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        int count = repository.getPostsCountByTag(ZonedDateTime.now(), tag);
        postResponse.setCount(count);
        if (count == 0){
            postResponse.setPosts(new ArrayList<>());
            return postResponse;
        }
        postResponse.setPosts(repository.getPostsForPostResponseByTag(ZonedDateTime.now(), tag, page));
        return postResponse;
    }
}
