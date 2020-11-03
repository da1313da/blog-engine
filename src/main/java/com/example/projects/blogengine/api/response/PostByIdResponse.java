package com.example.projects.blogengine.api.response;

import com.example.projects.blogengine.data.CommentForPostById;
import com.example.projects.blogengine.data.UserForResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostByIdResponse {
    Integer id;
    Long timestamp;
    Boolean active;
    UserForResponse user;
    String title;
    String text;
    Integer likeCount;
    Integer dislikeCount;
    Integer viewCount;
    List<CommentForPostById> comments;
    List<String> tags;
}
