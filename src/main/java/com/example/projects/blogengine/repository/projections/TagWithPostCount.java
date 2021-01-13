package com.example.projects.blogengine.repository.projections;


import com.example.projects.blogengine.model.Tag;

public interface TagWithPostCount {

    Tag getTag();

    int getPostCount();

}
