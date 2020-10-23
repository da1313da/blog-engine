package com.example.projects.blogengine.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class PostsDto {
    private Integer id;
    private Long timestamp;
    private UserDto user;
    private String title;
    private String announce;
    private Long likeCount;
    private Long dislikeCount;
    private Long commentCount;
    private Integer viewCount;
}
