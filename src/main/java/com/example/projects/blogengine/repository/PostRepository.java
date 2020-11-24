package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.projections.CalendarStatistics;
import com.example.projects.blogengine.repository.projections.PostsStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " left join p.comments pc" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by pc.size desc")
    List<Post> getPopularPosts(Pageable page);

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " left join p.likes pl" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by pl.size desc")
    List<Post> getBestPosts(Pageable page);

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by p.time")
    List<Post> getEarlyPosts(Pageable page);


    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by p.time desc")
    List<Post> getRecentPosts(Pageable page);

    @Query("select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getPostsCount();

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() and p.time between ?1 and ?2")
    List<Post> getPostsByDate(ZonedDateTime start, ZonedDateTime end, Pageable page);

    @Query("select count(p) from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() and p.time between ?1 and ?2")
    int getPostsCountByDate(ZonedDateTime start, ZonedDateTime end);

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where ?1 = any(select t.name from p.tags t) and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    List<Post> getPostsByTag(String tag, Pageable pageable);

    @Query("select count(p) from Post p" +
            " where ?1 = any(select t.name from p.tags t) and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getPostsCountByTag(String tag);

//    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
//    @Query("select p from Post p where p.id = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
//    Optional<Post> getPostById(int id);

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.id = ?1")
    Optional<Post> getPostById(int id);

    @EntityGraph(attributePaths = {"user", "moderator", "tags"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.id = ?1")
    Optional<Post> getPostByIdPreloadTags(int id);

    @Query("select p from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    Stream<Post> getPostsStream();

    @EntityGraph(attributePaths = {"user", "likes.user", "disLikes.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p")
    Stream<Post> getPostsStreamFetchVotes();

    @EntityGraph(attributePaths = {"user", "likes.user", "disLikes.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.user.id = ?1")
    List<Post> getUserPostsFetchVotes(int id);

    @Query("select date(p.time) as date, count(p.time) as count from Post p where year(p.time) = ?1 group by day(p.time)")
    List<CalendarStatistics> getPostCountPerDayInYear(Integer year);

    @Query("select distinct year(p.time) from Post p")
    List<Integer> getYears();

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " left join p.comments pc" +
            " where p.moderator = ?1 and p.isActive = 1 and p.moderationStatus = ?2" +
            " order by pc.size desc")
    List<Post> getModeratedPosts(User user, ModerationType status, Pageable pageable);

    @Query("select count(p) from Post p where p.moderator = ?1 and p.isActive = 1 and p.moderationStatus = ?2")
    int getModeratedPostCount(User user, ModerationType status);

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " left join p.comments pc" +
            " where p.user = ?1 and p.isActive = ?3 and p.moderationStatus in ?2" +
            " order by pc.size desc")
    List<Post> getUserPosts(User user, List<ModerationType> status, byte isActive,  Pageable pageable);

    @Query("select count(p) from Post p where p.user = ?1 and p.isActive = ?3 and p.moderationStatus in ?2")
    int getUserPostsCount(User user, List<ModerationType> status, byte isActive);

    @Query(value = "select count(*) as postCount," +
            " sum(ps.likes) as likesCount," +
            " sum(ps.dislikes) as dislikesCount," +
            " sum(ps.views) as viewsCount," +
            " ps.post_time as firstPublication" +
            " from" +
            " (select" +
            " p.time as post_time," +
            " count(distinct case pv.value when 1 then pv.id end) as likes," +
            " count(distinct case pv.value when -1 then pv.id end) as dislikes," +
            " p.view_count as views" +
            " from posts p" +
            " left join post_comments pc on p.id = pc.post_id" +
            " left join post_votes pv on p.id = pv.post_id" +
            " group by p.id order by p.time asc) as ps", nativeQuery = true)
    PostsStatistics getGlobalStatistics();

    @Query(value = "select count(*) as postCount," +
            " sum(ps.likes) as likesCount," +
            " sum(ps.dislikes) as dislikesCount," +
            " sum(ps.views) as viewsCount," +
            " ps.post_time as firstPublication" +
            " from" +
            " (select" +
            " p.time as post_time," +
            " count(distinct case pv.value when 1 then pv.id end) as likes," +
            " count(distinct case pv.value when -1 then pv.id end) as dislikes," +
            " p.view_count as views" +
            " from posts p" +
            " left join post_comments pc on p.id = pc.post_id" +
            " left join post_votes pv on p.id = pv.post_id" +
            " where p.user_id = ?1" +
            " group by p.id order by p.time asc) as ps", nativeQuery = true)
    PostsStatistics getUserStatistics(int id);


}
