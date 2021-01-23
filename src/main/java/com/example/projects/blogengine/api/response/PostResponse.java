package com.example.projects.blogengine.api.response;

import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    private int id;
    private long timestamp;
    private boolean active;
    private UserPostResponse user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<CommentListResponse> comments;
    private List<String> tags;
}
