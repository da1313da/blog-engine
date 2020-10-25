package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.repository.view.PostsByConditionView;
import com.example.projects.blogengine.model.views.PostsByConditions;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostsByConditionRepository extends CrudRepository<PostsByConditions, Integer> {
    @Query("select p from PostsByConditions p")
    List<PostsByConditionView> getPostsByConditions(Pageable pageable);
    @Query("select count(p) from PostsByConditions p")
    int getPostsCount();
}
