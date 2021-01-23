package com.example.projects.blogengine.api.response;

import lombok.Data;

@Data
public class UserCommentResponse {
    private int id;
    private String name;
    private String photo;
}
