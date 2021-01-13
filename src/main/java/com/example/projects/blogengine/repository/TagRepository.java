package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.repository.projections.TagWithPostCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("select t from Tag t where t.name in ?1")
    List<Tag> getTagsByName(List<String> names);

    @Query("select t as tag, size(t.posts) as postCount from Tag t")
    List<TagWithPostCount> getTagsWithPostCount();

    @Query("select t as tag, size(t.posts) as postCount from Tag t where t.name like ?1%")
    List<TagWithPostCount> getTagsWithPostCountBySearchQuery(String query);

}
