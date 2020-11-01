package com.example.projects.blogengine.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"tags", "comments", "votes"})
@Entity
@Table(name = "posts")
public class Post {
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
    private User moderator;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private ZonedDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private Integer viewCount;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "Tag2Post",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private Set<Tag> tags = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private Set<PostComment> comments = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private Set<PostVote> votes = new HashSet<>();
}