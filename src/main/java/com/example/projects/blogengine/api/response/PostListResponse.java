package com.example.projects.blogengine.api.response;

import lombok.Data;

import java.util.List;

@Data
public class PostListResponse {
    private int count;
    private List<PostAnnounceResponse> posts;
}
