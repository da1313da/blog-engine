package com.example.projects.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private ZonedDateTime time;

    @Column(nullable = false)
    private Byte value;

    @PrePersist
    private void prePersis(){
        time = ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
