package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.repository.projections.TagWithPostCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("select t from Tag t where t.name in ?1")
    List<Tag> getTagsByName(List<String> names);

    @Query(value = "select t.id as tagId, t.name as tagName, count(t.id) as postCount" +
            " from tags t join tag2post ttp on ttp.tag_id = t.id join posts p on p.id = ttp.post_id" +
            " where p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < now() group by t.id", nativeQuery = true)
    List<TagWithPostCount> getTagsWithPostCount();

    @Query(value = "select t.id as tagId, t.name as tagName, count(t.id) as postCount" +
            " from tags t join tag2post ttp on ttp.tag_id = t.id join posts p on p.id = ttp.post_id" +
            " where t.name like ?1% and p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < now() group by t.id",
            nativeQuery = true)
    List<TagWithPostCount> getTagsWithPostCountBySearchQuery(String query);

}
