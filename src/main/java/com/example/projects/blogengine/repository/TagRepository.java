package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Query(value = "select t.name from tags t join tag2post ttp on t.id = ttp.tag_id where ttp.post_id = ?1", nativeQuery = true)
    List<String> getByPost(Integer postId);
}
