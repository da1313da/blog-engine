package com.example.projects.blogengine.api.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePostRequest {
    Long timestamp;
    int active;
    String title;
    List<String> tags;
    String text;
}
