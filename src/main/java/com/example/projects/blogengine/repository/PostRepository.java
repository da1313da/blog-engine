package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.data.PostForPostByIdResponse;
import com.example.projects.blogengine.data.PostForPostResponse;
import com.example.projects.blogengine.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("select p.id as id," +
            " p.time as time," +
            " p.user as user," +
            " p.title as title," +
            " p.text as text," +
            " sum(case pv.value when 1 then 1 else 0 end) / count(distinct pc.id) as likes," +
            " sum(case pv.value when -1 then 1 else 0 end) / count(distinct pc.id) as dislikes," +
            " count(distinct pc.id) as comments," +
            " p.viewCount as viewCount" +
            " from Post p left join PostComment pc on p.id = pc.post left join PostVote pv on p.id = pv.post" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 group by p.id")
    List<PostForPostResponse> getPostsForPostResponse(ZonedDateTime now, Pageable pageable);

    @Query("select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1")
    int getPostsCount(ZonedDateTime now);

    @Query("select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and p.time between ?2 and ?3")
    int getPostsCountByDate(ZonedDateTime now, ZonedDateTime start, ZonedDateTime end);

    //todo
    @Query(value = "select count(p.id)" +
            " from posts p join tag2post ttp on p.id = ttp.post_id join tags t on ttp.tag_id = t.id" +
            " where p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < ?1 and t.name = ?2", nativeQuery = true)
    int getPostsCountByTag(String now, String tag);

    @Query("select p.id as id," +
            " p.time as time," +
            " p.user as user," +
            " p.title as title," +
            " p.text as text," +
            " sum(case pv.value when 1 then 1 else 0 end) / count(distinct pc.id) as likes," +
            " sum(case pv.value when -1 then 1 else 0 end) / count(distinct pc.id) as dislikes," +
            " count(distinct pc.id) as comments," +
            " p.viewCount as viewCount" +
            " from Post p left join PostComment pc on p.id = pc.post left join PostVote pv on p.id = pv.post" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and p.time between ?2 and ?3 group by p.id")
    List<PostForPostResponse> getPostsForPostResponseByDate(ZonedDateTime now, ZonedDateTime start, ZonedDateTime end , Pageable pageable);

    @Query( value = "select p.id as id," +
            " p.time as time," +
            " p.user_id as user," +
            " p.title as title," +
            " p.text as text," +
            " coalesce(sum(case pv.value when 1 then 1 else 0 end) / count(distinct pc.id), 0) as likes," +
            " coalesce(sum(case pv.value when -1 then 1 else 0 end) / count(distinct pc.id), 0) as dislikes," +
            " count(distinct pc.id) as comments," +
            " p.view_count as viewCount" +
            " from posts p left join post_comments pc on p.id = pc.post_id left join post_votes pv on p.id = pv.post_id " +
            " where p.id in " +
            " (select p.id from posts p join tag2post ttp on p.id = ttp.post_id join tags t on ttp.tag_id = t.id where p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < ?1 and t.name = ?2)" +
            " group by p.id", nativeQuery = true)
    List<PostForPostResponse> getPostsForPostResponseByTag(String now, String tag, Pageable pageable);

    @Query("select p.id as id," +
            " p.time as time," +
            " p.user as user," +
            " p.moderator as moderator," +
            " p.title as title," +
            " p.text as text," +
            " coalesce(sum(case pv.value when 1 then 1 else 0 end) / count(distinct pc.id), 0) as likes," +
            " coalesce(sum(case pv.value when -1 then 1 else 0 end) / count(distinct pc.id), 0) as dislikes," +
            " count(distinct pc.id) as comments," +
            " p.viewCount as viewCount," +
            " p.isActive as isActive" +
            " from Post p left join PostComment pc on p.id = pc.post left join PostVote pv on p.id = pv.post" +
            " where p.id = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?2 group by p.id")
    Optional<PostForPostByIdResponse> getPostsForPostByIdResponse(Integer id, ZonedDateTime now);

    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = ?1")
    void increaseViewCount(Integer id);
}
