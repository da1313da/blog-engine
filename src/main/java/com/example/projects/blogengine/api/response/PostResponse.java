package com.example.projects.blogengine.api.response;

import com.example.projects.blogengine.data.PostForPostResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponse {
    Integer count;
    List<PostForPostResponse> posts;
}
