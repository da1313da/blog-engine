package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostListResponse {
    int count;
    List<PostAnnounceResponse> posts;
}
