package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
}
