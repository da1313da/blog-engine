package com.example.projects.blogengine.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tag2post")
public class TagToPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Posts postId;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tags tagId;
}
