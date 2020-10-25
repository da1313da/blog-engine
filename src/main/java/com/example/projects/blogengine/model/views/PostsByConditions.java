package com.example.projects.blogengine.model.views;

import com.example.projects.blogengine.model.Users;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import javax.persistence.*;

@Immutable
@Entity
@Data
@Subselect("SELECT p.id," +
        " p.time as timestamp," +
        " p.title," +
        " p.user_id," +
        " p.text as announce," +
        " sum(case when pv.value = 1 then 1 else 0 end) / count(distinct pc.id) as like_count," +
        " sum(case when pv.value = -1 then 1 else 0 end) / count(distinct pc.id) as dislike_count," +
        " count(distinct pc.id) as comment_count," +
        " p.view_count as view_count" +
        " FROM posts p" +
        " LEFT JOIN post_comments pc ON p.id = pc.post_id" +
        " LEFT JOIN post_votes pv ON p.id = pv.post_id" +
        " WHERE p.is_active = 1" +
        " AND" +
        " p.moderation_status = 'ACCEPTED'" +
        " AND" +
        " p.time < NOW()" +
        " GROUP BY p.id")
@Synchronize({"posts", "post_comments", "post_votes"})
public class PostsByConditions {
    @Id
    private Integer id;

    @Convert(converter = ZoneTimeToUtsConverter.class)
    private Long timestamp;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private String title;

    @Convert(converter = PostsTextToAnnounceConverter.class)
    private String announce;

    @Convert(converter = NullToZeroConverter.class)
    @Column(name = "like_count")
    private Integer likeCount;

    @Convert(converter = NullToZeroConverter.class)
    @Column(name = "dislike_count")
    private Integer dislikeCount;

    @Convert(converter = NullToZeroConverter.class)
    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "view_count")
    private Integer viewCount;
}
