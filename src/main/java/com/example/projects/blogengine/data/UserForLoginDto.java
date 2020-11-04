package com.example.projects.blogengine.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForLoginDto {
    Integer id;
    String name;
    String photo;
    String email;
    Boolean moderation;
    Integer moderationCount;
    Boolean settings;
}
