package com.example.projects.blogengine.repository.projections;

import com.example.projects.blogengine.model.Post;

public interface PostWithStatistics {
    Post getPost();
    int getCommentCount();
    int getLikes();
    int getDislikes();
}
