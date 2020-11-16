package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    @EntityGraph(attributePaths = {"posts.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select t from Tag t")
    List<Tag> getTags();

    @EntityGraph(attributePaths = {"posts.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select t from Tag t where t.name like ?1%")
    List<Tag> getTagStartsWith(String string);
}
