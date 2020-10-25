package com.example.projects.blogengine.repository.view;

public interface PostsByConditionView {
    Integer getId();
    Long getTimestamp();
    UserView getUser();
    String getTitle();
    String getAnnounce();
    Integer getLikeCount();
    Integer getDislikeCount();
    Integer getCommentCount();
    Integer getViewCount();
}
