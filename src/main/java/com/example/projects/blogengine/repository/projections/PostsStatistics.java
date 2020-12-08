package com.example.projects.blogengine.repository.projections;

public interface PostsStatistics {
    Integer getPostsCount();
    int getLikesCount();
    int getDislikesCount();
    int getViewsCount();
    long getFirstPublication();
}