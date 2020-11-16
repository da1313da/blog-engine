package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentListResponse {
    int id;
    long timestamp;
    String text;
    UserCommentResponse user;
}
