package com.example.projects.blogengine.data;

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
