package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponse {
    int id;
    String name;
    String photo;
    String email;
    boolean moderation;
    int moderationCount;
    boolean settings;
}
