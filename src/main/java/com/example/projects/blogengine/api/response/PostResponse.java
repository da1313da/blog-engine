package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponse {
    int id;
    long timestamp;
    boolean active;
    UserPostResponse user;
    String title;
    String text;
    int likeCount;
    int dislikeCount;
    int viewCount;
    List<CommentListResponse> comments;
    List<String> tags;
}
