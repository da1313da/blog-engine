package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.projections.CalendarStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = ?1")
    void increaseViewCount(Integer id);

    @Query("select p from Post p where ?1 member of p.tags")
    List<Post> getTest(Tag t);

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

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.id = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    Optional<Post> getPostById(int id);

    @Query("select p from Post p")
    Stream<Post> getPostsStream();

    @Query("select date(p.time) as date, count(p.time) as count from Post p where year(p.time) = ?1 group by day(p.time)")
    List<CalendarStatistics> getPostCountPerDayInYear(Integer year);

    @Query("select distinct year(p.time) from Post p")
    List<Integer> getYears();

    @Query("select count(p) from Post p where p.moderationStatus = 'NEW' and p.moderator = ?1")
    int getPostCountModeratedByUser(User user);
}
