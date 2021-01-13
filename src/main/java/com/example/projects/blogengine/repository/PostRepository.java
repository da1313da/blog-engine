package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.projections.CalendarStatistics;
import com.example.projects.blogengine.repository.projections.PostWithStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p from Post p join fetch p.user" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() order by p.time desc")
    List<Post> getRecentPosts(Pageable pageable);

    @Query("select p from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() order by p.time asc")
    List<Post> getEarlyPosts(Pageable pageable);

    @Query("select p from Post p join fetch p.user" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() order by size(p.comments) desc")
    List<Post> getPopularPosts(Pageable pageable);

    @Query("select p, (select count(pv) from PostVote pv where pv.value = 1 and pv.post = p) as likes" +
            " from Post p join fetch p.user where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() order by likes desc")
    List<Post> getBestPost(Pageable pageable);

    @Query("select p from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() and p.text like %?1% or p.title like %?1%")
    List<Post> getPostListBySearchWord(String searchWord, Pageable pageable);

    @Query("select count(p) from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() and p.text like %?1% or p.title like %?1%")
    int getPostListCountBySearchWord(String searchWord);

    @Query("select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getPostCount();

    @Query("select p from Post p join fetch p.user" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() and p.time between ?1 and ?2")
    List<Post> getPostsByDate(ZonedDateTime start, ZonedDateTime end, Pageable page);

    @Query("select count(p) from Post p" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now() and p.time between ?1 and ?2")
    int getPostsCountByDate(ZonedDateTime start, ZonedDateTime end);

    @Query("select p from Post p join fetch p.user join p.tags t" +
            " where t.name = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    List<Post> getPostsByTag(String tag, Pageable pageable);

    @Query("select count(p) from Post p join p.tags t" +
            " where t.name = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    int getPostsCountByTag(String tag);

    @EntityGraph(attributePaths = {"user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.id = ?1")
    Optional<Post> getPostById(int id);

    @EntityGraph(attributePaths = {"user", "moderator", "tags"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p where p.id = ?1")
    Optional<Post> getPostByIdPreloadTags(int id);

    @Query("select p as post," +
            " (select count(pc) from PostComment pc where pc.post = p) as commentCount," +
            " (select count(pv) from PostVote pv where pv.post = p and pv.value = 1) as likes," +
            " (select count(pv) from PostVote pv where pv.post = p and pv.value = -1) as dislikes" +
            " from Post p join fetch p.user where p.isActive = 1 and p.moderationStatus = 'NEW'")
    List<PostWithStatistics> getNewActivePosts(Pageable pageable);

    int countByIsActiveAndModerationStatus(byte isActive, ModerationType moderationType);

    @Query("select p as post," +
            " (select count(pc) from PostComment pc where pc.post = p) as commentCount," +
            " (select count(pv) from PostVote pv where pv.post = p and pv.value = 1) as likes," +
            " (select count(pv) from PostVote pv where pv.post = p and pv.value = -1) as dislikes" +
            " from Post p join fetch p.user where p.user = ?1 and p.isActive = ?3 and p.moderationStatus in ?2")
    List<PostWithStatistics> getUserPosts(User user, List<ModerationType> status, byte isActive, Pageable pageable);

    @Query("select p as post," +
            " (select count(pc) from PostComment pc where pc.post = p) as commentCount," +
            " (select count(pv) from PostVote pv where pv.post = p and pv.value = 1) as likes," +
            " (select count(pv) from PostVote pv where pv.post = p and pv.value = -1) as dislikes" +
            " from Post p join fetch p.user where p.moderator = ?1 and p.isActive = 1 and p.moderationStatus = ?2")
    List<PostWithStatistics> getPostsModeratedByUser(User user, ModerationType moderationType, Pageable pageable);

    @Query("select count(p) from Post p where p.moderator = ?1 and p.isActive = 1 and p.moderationStatus = ?2")
    int getPostsModeratedByUserCount(User user, ModerationType moderationType);

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
    List<Integer> getYearsWithActivePosts();

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
    Optional<Integer> getAllViewCount();

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
    Optional<Integer> getUserViewCount(int userId);

    @Query("select p.time" +
            " from Post p where p.user.id = ?1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()")
    List<ZonedDateTime> getUserPostFirstPublicationTime(int userId, Pageable pageable);

}
