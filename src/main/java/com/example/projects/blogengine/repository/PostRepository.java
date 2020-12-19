package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.projections.CalendarStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p, u, m from Post p" +
            " join p.user u" +
            " left join p.moderator m" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by size(p.comments) desc")
    List<Post> getPopularPosts1(Pageable page);
    @EntityGraph(attributePaths = {"likes.user", "disLikes.user","comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p in ?1" +
            " order by size(p.comments) desc")
    List<Post> getPopularPosts2(List<Post> first);

    @Query("select p, u, m from Post p" +
            " join p.user u" +
            " left join p.moderator m" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by p.likes.size desc")
    List<Post> getBestPosts1(Pageable page);
    @EntityGraph(attributePaths = {"likes.user", "disLikes.user","comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p in ?1" +
            " order by p.likes.size desc")
    List<Post> getBestPosts2(List<Post> first);

    @Query("select p, u, m from Post p" +
            " join p.user u" +
            " left join p.moderator m" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by (p.time) asc")
    List<Post> getEarlyPosts1(Pageable page);
    @EntityGraph(attributePaths = {"likes.user", "disLikes.user","comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p in ?1" +
            " order by (p.time) asc")
    List<Post> getEarlyPosts2(List<Post> first);

    @Query("select p, u, m from Post p" +
            " join p.user u" +
            " left join p.moderator m" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by p.time desc")
    List<Post> getRecentPosts1(Pageable page);
    @EntityGraph(attributePaths = {"likes.user", "disLikes.user","comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p in ?1" +
            " order by p.time desc")
    List<Post> getRecentPosts2(List<Post> first);

    @EntityGraph(attributePaths = {"user", "moderator"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.text like %?1% and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    List<Post> getPostsByQuery(String query, Pageable pageable);

    @EntityGraph(attributePaths = {"likes.user", "disLikes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p in ?1")
    List<Post> getPostsByQuery(List<Post> initial);

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

    @EntityGraph(attributePaths = {"user", "moderator"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p join p.tags t where t.name = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    List<Post> getPostsByTag(String tag, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "likes.user", "disLikes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p in ?1")
    List<Post> getPostsByTag1(List<Post> initial);

    @Query("select count(p) from Post p" +
            " where ?1 = any(select t.name from p.tags t) and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getPostsCountByTag(String tag);

    @EntityGraph(attributePaths = {"user", "likes.user", "disLikes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.id = ?1")
    Optional<Post> getPostById(int id);

    @EntityGraph(attributePaths = {"user", "moderator", "tags"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.id = ?1")
    Optional<Post> getPostByIdPreloadTags(int id);


    @EntityGraph(attributePaths = {"user", "likes.user", "disLikes.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.user.id = ?1")
    List<Post> getUserPostsFetchVotes(int id);

    @EntityGraph(attributePaths = {"user", "moderator"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p.moderator = ?1 and p.isActive = 1 and p.moderationStatus = ?2")
    List<Post> getModeratedPosts(User user, ModerationType status, Pageable pageable);

    @EntityGraph(attributePaths = {"likes.user", "disLikes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p in ?1")
    List<Post> getModeratedPosts(List<Post> initial);

    @EntityGraph(attributePaths = {"user", "moderator"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p.isActive = 1 and p.moderationStatus = ?1")
    List<Post> getAllModeratedPosts(ModerationType status, Pageable pageable);

    @EntityGraph(attributePaths = {"likes.user", "disLikes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p in ?1")
    List<Post> getALlModeratedPosts(List<Post> initial);

    @Query("select count(p) from Post p where p.moderator = ?1 and p.isActive = 1 and p.moderationStatus = ?2")
    int getModeratedPostCount(User user, ModerationType status);

    @Query("select count(p) from Post p where p.isActive = 1 and p.moderationStatus = ?1")
    int getAllModeratedPostCount(ModerationType status);

    @EntityGraph(attributePaths = {"user","moderator"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p.user = ?1 and p.isActive = ?3 and p.moderationStatus in ?2" +
            " order by p.comments.size desc")
    List<Post> getUserPosts(User user, List<ModerationType> status, byte isActive,  Pageable pageable);

    @EntityGraph(attributePaths = {"user", "likes.user", "disLikes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p in ?1" +
            " order by p.comments.size desc")
    List<Post> getUserPosts(List<Post> first);

    @Query("select count(p) from Post p where p.user = ?1 and p.isActive = ?3 and p.moderationStatus in ?2")
    int getUserPostCount(User user, List<ModerationType> status, byte isActive);

    @Query("select count(p.id) as count," +
            " concat(year(p.time), '-', month(p.time), '-', day(p.time)) as date" +
            " from Post p" +
            " where year(p.time) = ?1 and" +
            " p.isActive = 1 and" +
            " p.moderationStatus = 'ACCEPTED' and" +
            " p.time < now()" +
            " group by concat(year(p.time), '-', month(p.time), '-', day(p.time))")
    List<CalendarStatistics> getPostCountPerDay(int year);

    @Query("select year(p.time)" +
            " from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() group by year(p.time)")
    List<Integer> getYears();

    @Query("select count(pv.id)" +
            " from PostVote pv" +
            " join pv.post p" +
            " where pv.value = 1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getAllLikesCount();

    @Query("select count(pv.id)" +
            " from PostVote pv" +
            " join pv.post p where pv.value = -1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getAllDislikesCount();

    @Query("select sum(p.viewCount)" +
            " from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getAllViewCount();

    @Query("select p.time from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    List<ZonedDateTime> getAllPostFirstPublicationTime(Pageable pageable);

    @Query("select count(p.id)" +
            " from Post p where p.user.id = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getUserPostCount(int userId);

    @Query("select count(pv.id)" +
            " from PostVote pv join pv.post p" +
            " where p.user.id = ?1 and pv.value = 1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getUserLikesCount(int userId);

    @Query("select count(pv.id)" +
            " from PostVote pv join pv.post p" +
            " where p.user.id = ?1 and pv.value = -1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getUserDislikesCount(int userId);

    @Query("select sum(p.viewCount)" +
            " from Post p where p.user.id = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getUserViewCount(int userId);

    @Query("select p.time" +
            " from Post p where p.user.id = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    List<ZonedDateTime> getUserPostFirstPublicationTime(int userId, Pageable pageable);

}
