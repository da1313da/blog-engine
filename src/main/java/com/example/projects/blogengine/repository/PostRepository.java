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

    @Query("select p, u, m from Post p" +
            " join p.user u" +
            " left join p.moderator m" +
            " where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time < now()" +
            " order by size(p.comments) desc")
    List<Post> getPopularPosts1(Pageable page);
    @EntityGraph(attributePaths = {"votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
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
    @EntityGraph(attributePaths = {"votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
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
    @EntityGraph(attributePaths = {"votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
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
    @EntityGraph(attributePaths = {"votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " where p in ?1" +
            " order by p.time desc")
    List<Post> getRecentPosts2(List<Post> first);

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

    @EntityGraph(attributePaths = {"user", "likes.user", "disLikes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
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

    @Query(value = "SELECT DISTINCT\n" +
            "    DATE_FORMAT(p.time, '%Y')\n" +
            "FROM\n" +
            "    posts p\n" +
            "WHERE\n" +
            "    p.is_active = 1\n" +
            "        AND p.moderation_status = 'ACCEPTED'\n" +
            "        AND p.time < NOW(0)\n" +
            "ORDER BY DATE_FORMAT(p.time, '%Y')", nativeQuery = true)
    List<Integer> getYears();

    @EntityGraph(attributePaths = {"user", "votes.user", "comments.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select p from Post p" +
            " left join p.comments pc" +
            " where p.moderator = ?1 and p.isActive = 1 and p.moderationStatus = ?2" +
            " order by pc.size desc")
    List<Post> getModeratedPosts(User user, ModerationType status, Pageable pageable);

    @Query("select count(p) from Post p where p.moderator = ?1 and p.isActive = 1 and p.moderationStatus = ?2")
    int getModeratedPostCount(User user, ModerationType status);

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
    int getUserPostsCount(User user, List<ModerationType> status, byte isActive);

    @Query(value = "SELECT \n" +
            "    DATE_FORMAT(p.time, '%Y-%m-%d') AS date,\n" +
            "    COUNT(p.time) AS count\n" +
            "FROM\n" +
            "    posts p\n" +
            "WHERE\n" +
            "    p.is_active = 1\n" +
            "        AND p.moderation_status = 'ACCEPTED'\n" +
            "        AND p.time < NOW(0)\n" +
            "        AND DATE_FORMAT(p.time, '%Y') = ?1\n" +
            "GROUP BY DATE_FORMAT(p.time, '%Y-%m-%d')" , nativeQuery = true)
    List<CalendarStatistics> getPostCountPerDayInYear(Integer year);

    @Query(value = "SELECT \n" +
            "    COUNT(*) AS postsCount,\n" +
            "    SUM(ps.likes) AS likesCount,\n" +
            "    SUM(ps.dislikes) AS dislikesCount,\n" +
            "    SUM(ps.views) AS viewsCount,\n" +
            "    UNIX_TIMESTAMP(ps.post_time) AS firstPublication\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        p.time AS post_time,\n" +
            "            COUNT(DISTINCT CASE pv.value\n" +
            "                WHEN 1 THEN pv.id\n" +
            "            END) AS likes,\n" +
            "            COUNT(DISTINCT CASE pv.value\n" +
            "                WHEN - 1 THEN pv.id\n" +
            "            END) AS dislikes,\n" +
            "            p.view_count AS views\n" +
            "    FROM\n" +
            "        posts p\n" +
            "    LEFT JOIN post_comments pc ON p.id = pc.post_id\n" +
            "    LEFT JOIN post_votes pv ON p.id = pv.post_id\n" +
            "    WHERE\n" +
            "        p.is_active = 1\n" +
            "            AND p.moderation_status = 'ACCEPTED'\n" +
            "            AND p.time < NOW(0)\n" +
            "    GROUP BY p.id\n" +
            "    ORDER BY p.time ASC) AS ps", nativeQuery = true)
    PostsStatistics getGlobalStatistics();

    @Query(value = "SELECT \n" +
            "    COUNT(*) AS postsCount,\n" +
            "    SUM(ps.likes) AS likesCount,\n" +
            "    SUM(ps.dislikes) AS dislikesCount,\n" +
            "    SUM(ps.views) AS viewsCount,\n" +
            "    UNIX_TIMESTAMP(ps.post_time) AS firstPublication\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        p.time AS post_time,\n" +
            "            COUNT(DISTINCT CASE pv.value\n" +
            "                WHEN 1 THEN pv.id\n" +
            "            END) AS likes,\n" +
            "            COUNT(DISTINCT CASE pv.value\n" +
            "                WHEN - 1 THEN pv.id\n" +
            "            END) AS dislikes,\n" +
            "            p.view_count AS views\n" +
            "    FROM\n" +
            "        posts p\n" +
            "    LEFT JOIN post_comments pc ON p.id = pc.post_id\n" +
            "    LEFT JOIN post_votes pv ON p.id = pv.post_id\n" +
            "    WHERE\n" +
            "        p.user_id = ?1 AND p.is_active = 1\n" +
            "            AND p.moderation_status = 'ACCEPTED'\n" +
            "            AND p.time < NOW(0)\n" +
            "    GROUP BY p.id\n" +
            "    ORDER BY p.time ASC) AS ps", nativeQuery = true)
    PostsStatistics getUserStatistics(int id);
}
