package com.example.projects.blogengine.api.response;

import lombok.Data;

@Data
public class CommentListResponse {
    private int id;
    private long timestamp;
    private String text;
    private UserCommentResponse user;
}
