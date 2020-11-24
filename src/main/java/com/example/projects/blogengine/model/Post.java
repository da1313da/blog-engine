package com.example.projects.blogengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"tags", "comments", "votes", "likes", "disLikes"})
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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne(cascade = CascadeType.PERSIST)
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

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "tag2post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private Set<PostComment> comments = new HashSet<>();

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private Set<PostVote> votes = new HashSet<>();

    @JsonIgnore
    @ToString.Exclude
    @Where(clause = "value = 1")
    @OneToMany(mappedBy = "post")
    private Set<PostVote> likes = new HashSet<>();

    @JsonIgnore
    @ToString.Exclude
    @Where(clause = "value = -1")
    @OneToMany(mappedBy = "post")
    private Set<PostVote> disLikes = new HashSet<>();

    public void addTag(Tag tag){
        tags.add(tag);
        tag.getPosts().add(this);
    }

    public void removeTag(Tag tag){
        tags.remove(tag);
        tag.getPosts().remove(this);
    }
}