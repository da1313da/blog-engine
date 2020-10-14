package com.example.projects.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
public class PostComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //FK, nullable
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private PostComments parentId;
    //FK
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Posts postId;
    //FK
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;
    @Column(nullable = false)
    private ZonedDateTime time;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
}
