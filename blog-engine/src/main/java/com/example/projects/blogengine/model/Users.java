package com.example.projects.blogengine.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {"regularPosts", "moderationPosts", "comments", "votes"})
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Byte isModerator;

    @Column(nullable = false)
    private ZonedDateTime regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @ToString.Exclude
    @OneToMany(mappedBy = "userId", cascade = CascadeType.PERSIST)
    private Set<Posts> regularPosts = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "moderatorId", cascade = CascadeType.PERSIST)
    private Set<Posts> moderationPosts = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "userId", cascade = CascadeType.PERSIST)
    private Set<PostComments> comments = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "userId", cascade = CascadeType.PERSIST)
    private Set<PostVotes> votes = new HashSet<>();
}
