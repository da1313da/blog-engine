package com.example.projects.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
public class PostVotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //FK
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;
    //FK
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Posts postId;
    @Column(nullable = false)
    private ZonedDateTime time;
    @Column(nullable = false)
    private Byte value;
}
