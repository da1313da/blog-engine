package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.User;
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
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by size(p.comments) desc")
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

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.text like %?1% and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    List<Post> getPostsByQuery(String query, Pageable pageable);

    @Query("select count(p) from Post p where p.text like %?1% and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getPostCountByQuery(String query);

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
}
