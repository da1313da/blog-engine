package com.example.projects.blogengine.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"linkedTags", "comments", "votes"})
@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Byte isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModerationType moderationStatus;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "moderator_id", nullable = false)
    private Users moderatorId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    @Column(nullable = false)
    private ZonedDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private Integer viewCount;

    @ToString.Exclude
    @OneToMany(mappedBy = "postId", cascade = CascadeType.PERSIST)
    private Set<TagToPost> linkedTags = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "postId", cascade = CascadeType.PERSIST)
    private Set<PostComments> comments = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "postId", cascade = CascadeType.PERSIST)
    private Set<PostVotes> votes = new HashSet<>();
}
