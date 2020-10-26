package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.data.PostsForPostResponse;
import com.example.projects.blogengine.model.Posts;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Integer> {

    @Query("select p.id as id," +
            " p.time as time," +
            " p.userId as user," +
            " p.title as title," +
            " p.text as text," +
            " sum(case pv.value when -1 then 1 end) / count(distinct pc.id) as likes," +
            " sum(case pv.value when 1 then 1 end) / count(distinct pc.id) as dislikes," +
            " count(distinct pc.id) as comments," +
            " p.viewCount as viewCount" +
            " from Posts p left join PostComments pc on p.id = pc.postId left join PostVotes pv on p.id = pv.postId" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 group by p.id")
    List<PostsForPostResponse> getPostsForPostResponse(ZonedDateTime now, Pageable pageable);

    @Query("select count(p) from Posts p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1")
    int getPostsCount(ZonedDateTime now);

    @Query("select count(p) from Posts p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and p.time between ?2 and ?3")
    int getPostsCountByDate(ZonedDateTime now, ZonedDateTime start, ZonedDateTime end);

    @Query("select count(p) from Posts p join TagToPost ttp on p.id = ttp.postId join Tags t on ttp.tagId = t.id where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and t.name = ?2")
    int getPostsCountByTag(ZonedDateTime now, String tag);

    @Query("select p.id as id," +
            " p.time as time," +
            " p.userId as user," +
            " p.title as title," +
            " p.text as text," +
            " sum(case pv.value when -1 then 1 end) / count(distinct pc.id) as likes," +
            " sum(case pv.value when 1 then 1 end) / count(distinct pc.id) as dislikes," +
            " count(distinct pc.id) as comments," +
            " p.viewCount as viewCount" +
            " from Posts p left join PostComments pc on p.id = pc.postId left join PostVotes pv on p.id = pv.postId" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and p.time between ?2 and ?3 group by p.id")
    List<PostsForPostResponse> getPostsForPostResponseByDate(ZonedDateTime now, ZonedDateTime start, ZonedDateTime end , Pageable pageable);

    @Query( "select p.id as id," +
            " p.time as time," +
            " p.userId as user," +
            " p.title as title," +
            " p.text as text," +
            " sum(case pv.value when -1 then 1 end) / count(distinct pc.id) as likes," +
            " sum(case pv.value when 1 then 1 end) / count(distinct pc.id) as dislikes," +
            " count(distinct pc.id) as comments," +
            " p.viewCount as viewCount" +
            " from Posts p left join PostComments pc on p.id = pc.postId left join PostVotes pv on p.id = pv.postId " +
            " where p.id in " +
            " (select p.id from Posts p join TagToPost ttp on p.id = ttp.postId join Tags t on ttp.tagId = t.id where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and t.name = ?2)" +
            " group by p.id")
    List<PostsForPostResponse> getPostsForPostResponseByTag(ZonedDateTime now, String tag, Pageable pageable);

}
