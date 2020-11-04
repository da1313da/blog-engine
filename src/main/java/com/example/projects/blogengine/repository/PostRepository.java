package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.data.PostForPostByIdResponse;
import com.example.projects.blogengine.data.PostForPostResponse;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.Tag;
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
            " count(distinct case pv.value when 1 then pv.id end) as likeCount," +
            " count(distinct case pv.value when -1 then pv.id end) as dislikeCount," +
            " count(distinct pc.id) as commentCount," +
            " p.viewCount as viewCount" +
            " from Post p left join PostComment pc on p.id = pc.post left join PostVote pv on p.id = pv.post" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 group by p.id")
    List<PostForPostResponse> getPostsForPostResponse(ZonedDateTime now, Pageable pageable);

    @Query("select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1")
    int getPostsCount(ZonedDateTime now);

    @Query("select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and p.time between ?2 and ?3")
    int getPostsCountByDate(ZonedDateTime now, ZonedDateTime start, ZonedDateTime end);

    @Query("select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and ?2 member of p.tags")
    int getPostsCountByTag(ZonedDateTime now, Tag tag);

    @Query("select p.id as id," +
            " p.time as time," +
            " p.user as user," +
            " p.title as title," +
            " p.text as text," +
            " count(distinct case pv.value when 1 then pv.id end) as likeCount," +
            " count(distinct case pv.value when -1 then pv.id end) as dislikeCount," +
            " count(distinct pc.id) as commentCount," +
            " p.viewCount as viewCount" +
            " from Post p left join PostComment pc on p.id = pc.post left join PostVote pv on p.id = pv.post" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and p.time between ?2 and ?3 group by p.id")
    List<PostForPostResponse> getPostsForPostResponseByDate(ZonedDateTime now, ZonedDateTime start, ZonedDateTime end , Pageable pageable);

    @Query("select p.id as id," +
            " p.time as time," +
            " p.user as user," +
            " p.title as title," +
            " p.text as text," +
            " count(distinct case pv.value when 1 then pv.id end) as likeCount," +
            " count(distinct case pv.value when -1 then pv.id end) as dislikeCount," +
            " count(distinct pc.id) as commentCount," +
            " p.viewCount as viewCount" +
            " from Post p left join PostComment pc on p.id = pc.post left join PostVote pv on p.id = pv.post " +
            " where p in " +
            " (select p from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?1 and ?2 member of p.tags)" +
            " group by p.id")
    List<PostForPostResponse> getPostsForPostResponseByTag(ZonedDateTime now, Tag tag, Pageable pageable);

    @Query("select p.id as id," +
            " p.time as time," +
            " p.user as user," +
            " p.moderator as moderator," +
            " p.title as title," +
            " p.text as text," +
            " count(distinct case pv.value when 1 then pv.id end) as likeCount," +
            " count(distinct case pv.value when -1 then pv.id end) as dislikeCount," +
            " p.viewCount as viewCount," +
            " p.isActive as isActive" +
            " from Post p left join PostComment pc on p.id = pc.post left join PostVote pv on p.id = pv.post" +
            " where p.id = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < ?2 group by p.id")
    Optional<PostForPostByIdResponse> getPostsForPostByIdResponse(Integer id, ZonedDateTime now);

    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = ?1")
    void increaseViewCount(Integer id);

    @Query("select p from Post p where ?1 member of p.tags")
    List<Post> getTest(Tag t);
}
