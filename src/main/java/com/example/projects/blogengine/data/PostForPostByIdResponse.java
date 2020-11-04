package com.example.projects.blogengine.data;

import com.example.projects.blogengine.model.User;

import java.time.ZonedDateTime;

public interface PostForPostByIdResponse {
    Integer getId();

    ZonedDateTime getTime();

    UserForResponse getUser();

    User getModerator();

    String getTitle();

    String getText();

    Integer getLikeCount();

    Integer getDislikeCount();

    Integer getViewCount();

    Byte getIsActive();
}
